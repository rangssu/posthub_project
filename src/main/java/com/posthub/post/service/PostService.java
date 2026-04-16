package com.posthub.post.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.posthub.board.domain.Board;
import com.posthub.board.repository.BoardRepository;
import com.posthub.common.exception.ForbiddenException;
import com.posthub.common.exception.NotFoundException;
import com.posthub.post.domain.Post;
import com.posthub.post.dto.PostListResponse;
import com.posthub.post.dto.PostRequest;
import com.posthub.post.dto.PostResponse;
import com.posthub.post.dto.PostUpdateRequest;
import com.posthub.post.repository.PostRepository;
import com.posthub.user.domain.User;
import com.posthub.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)  //트랜잭션 노션 확인.
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String RANKING_KEY = "post:ranking";
    private static final String POST_DETAIL_CACHE_KEY_PREFIX = "post:detail:";


    //Create 글작성
    @Transactional
    public Long createPost (Long boardId, Long userId, PostRequest request) {

        Board board = boardRepository.findById(boardId)
                .orElseThrow(()-> new IllegalArgumentException("보드 번호를 찾을수 없습니다 + " + boardId));
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException(("user not found + " + userId)));

        Post post = new Post(board, user, request.getTitle(), request.getContent());
        Post savedPost = postRepository.save(post);

        // 데이터 변경 시 해당 게시판의 리스트 캐시를 무효화하여 데이터 정합성 유지
        clearBoardPostCache(boardId);

        return savedPost.getId();
    }

    //Read 읽기 (✨ 상세 조회 캐싱 적용)
    @Transactional
    public PostResponse getPost(Long postId) {
        // 실시간 인기글 집계를 위해 조회 시마다 Redis ZSET Score 업데이트
        String redisKey = "post:viewCount:" + postId;
        redisTemplate.opsForValue().increment(redisKey);
        redisTemplate.opsForZSet().incrementScore(RANKING_KEY, String.valueOf(postId), 1);

        String cacheKey = POST_DETAIL_CACHE_KEY_PREFIX + postId;

        // Read-Aside 패턴 적용: 캐시 히트 시 DB 접근 없이 즉시 반환
        try {
            // 2. Redis에서 상세글 데이터가 있는지 확인 (Cache Hit)
            String cachedJson = redisTemplate.opsForValue().get(cacheKey);
            if (cachedJson != null) {
                // DB를 가지 않고 Redis에서 바로 객체를 반환! (이게 속도를 폭발적으로 줄여줍니다)
                return objectMapper.readValue(cachedJson, PostResponse.class);
            }
        } catch (JsonProcessingException e) {
            log.error("상세 조회 캐시 역직렬화 에러: {}", e.getMessage());
        }


        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        PostResponse response = PostResponse.from(post);

        // Cache Miss: DB 조회 결과를 캐시에 저장 (TTL 10분 설정)
        try {
            // 4. DB에서 찾은 데이터를 JSON으로 바꿔서 Redis에 저장 (10분 유지)
            String json = objectMapper.writeValueAsString(response);
            redisTemplate.opsForValue().set(cacheKey, json, Duration.ofMinutes(10));
        } catch (JsonProcessingException e) {
            log.error("상세 조회 캐시 직렬화 에러: {}", e.getMessage());
        }

        return response;
    }

    //Update 수정
    @Transactional
    public void update(Long userId, Long postId, PostUpdateRequest request) {
        Post post = getPostOrThrow(postId);
        validationAuthor(post, userId);
        post.update(request.getTitle(), request.getContent());

        clearBoardPostCache(post.getBoard().getId());
        redisTemplate.delete(POST_DETAIL_CACHE_KEY_PREFIX + postId);
    }

    //Delete 삭제
    @Transactional
    public void delete(Long userId, Long postId) {
        Post post = getPostOrThrow(postId);
        validationAuthor(post, userId);

        Long boardId = post.getBoard().getId();
        postRepository.deleteById(postId);

        clearBoardPostCache(boardId);
        redisTemplate.opsForZSet().remove(RANKING_KEY, String.valueOf(postId));
        redisTemplate.delete(POST_DETAIL_CACHE_KEY_PREFIX + postId);
    }

    // 긁 읽기 페이징 버전
    public Page<PostListResponse> getPostByBoard(Long boardId, Pageable pageable) {
        String cacheKey = "post:list:board:" + boardId + ":page:" + pageable.getPageNumber();

        try {
            String cachedJson = redisTemplate.opsForValue().get(cacheKey);
            if (cachedJson != null) {
                List<PostListResponse> content = objectMapper.readValue(cachedJson, new TypeReference<List<PostListResponse>>() {});

                return new PageImpl<>(content, pageable, 10000);// 부하 테스트를 위한 고정 카운트|
            }
        } catch (JsonProcessingException e) {
            log.error("Redis 캐시 역직렬화 중 에러 발생: {}", e.getMessage());
        }

        log.info(">>> [Cache Miss] 게시판 {}번의 {}페이지를 DB에서 조회합니다.", boardId, pageable.getPageNumber());
        Page<PostListResponse> responsePage = postRepository.findOptimizedByBoardId(boardId, pageable);

        try {
            // 3. DB에서 읽어온 데이터(content)를 JSON 문자열로 바꿔서 10분간 Redis에 저장합니다.
            String json = objectMapper.writeValueAsString(responsePage.getContent());
            redisTemplate.opsForValue().set(cacheKey, json, Duration.ofMinutes(10));
        } catch (JsonProcessingException e) {
            log.error("Redis 캐시 직렬화 중 에러 발생: {}", e.getMessage());
        }

        return responsePage;
    }

    /**
     * ✨ [추가됨] 실시간 인기글 TOP 10 조회 로직
     */
    public List<PostListResponse> getTop10TrendingPosts() {
        Set<String> topPostIds = redisTemplate.opsForZSet().reverseRange(RANKING_KEY, 0, 9);

        if (topPostIds == null || topPostIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> postIds = topPostIds.stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());

        List<PostListResponse> posts = postRepository.findOptimizedByIds(postIds);

        // Redis의 랭킹 순서를 보장하기 위해 재정렬 수행
        Map<Long, PostListResponse> postMap = posts.stream()
                .collect(Collectors.toMap(PostListResponse::getId, p -> p));

        return postIds.stream()
                .map(postMap::get)
                .filter(Objects::nonNull) // 혹시 DB에서 이미 지워진 글이 있다면 제외
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Post getPostOrThrow(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(()-> new NotFoundException("게시글을 찾을 수 없슴니다."));
    }

    // 작성자 확인 검증
    private void validationAuthor (Post post, Long requesterUserId) {
        if (!post.getUser().getId().equals(requesterUserId)) {
            throw new ForbiddenException("작성자만 수정/삭제 할 수있습니다.");
        }
    }

    /**
     * ✨ [추가됨] 게시글 추가/수정/삭제 시 해당 게시판의 전체 페이지 캐시를 일괄 삭제하는 헬퍼 메서드
     */
    private void clearBoardPostCache(Long boardId) {
        // 해당 게시판의 모든 페이지 캐시 키를 찾아 삭제합니다. (예: post:list:board:1:page:*)
        java.util.Set<String> keys = redisTemplate.keys("post:list:board:" + boardId + ":page:*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}