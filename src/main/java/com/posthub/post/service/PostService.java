package com.posthub.post.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.posthub.board.domain.Board;
import com.posthub.board.repository.BoardRepository;
import com.posthub.common.exception.ForbiddenException;
import com.posthub.common.exception.NotFoundException;
import com.posthub.post.dto.PostListResponse;
import com.posthub.post.dto.PostRequest;
import com.posthub.post.dto.PostResponse;
import com.posthub.post.domain.Post;
import com.posthub.post.dto.PostUpdateRequest;
import com.posthub.post.repository.PostRepository;
import com.posthub.user.domain.User;
import com.posthub.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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

    // ✨ [추가됨] Redis 캐시에 저장하기 위해 객체 <-> JSON 변환을 담당합니다.
    private final ObjectMapper objectMapper;

    // ✨ [추가됨] 실시간 인기글 랭킹을 저장할 Redis ZSET 키
    private static final String RANKING_KEY = "post:ranking";

//    public PostService(PostRepository postRepository,
//                       BoardRepository boardRepsitory,
//                       UserRepository userRepository) {
//        this.postRepository = postRepository;
//        this.boardRepository = boardRepsitory;
//        this.userRepository = userRepository;
//    }

    //Create 글작성
    @Transactional
    public Long createPost (Long boardId, Long userId, PostRequest request) {

        Board board = boardRepository.findById(boardId)
                .orElseThrow(()-> new IllegalArgumentException("보드 번호를 찾을수 없습니다 + " + boardId));
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException(("user not found + " + userId)));

        Post post = new Post(board, user, request.getTitle(), request.getContent());
        Post savedPost = postRepository.save(post);

        // ✨ [추가됨] 새 글이 작성되었으므로 해당 게시판의 목록 캐시를 비워줍니다.
        clearBoardPostCache(boardId);

        return savedPost.getId();
    }

    // ✨ [추가됨] 상세 조회용 캐시 키 접두사
    private static final String POST_DETAIL_CACHE_KEY_PREFIX = "post:detail:";

    //Read 읽기 (✨ 상세 조회 캐싱 적용)
    @Transactional
    public PostResponse getPost(Long postId) {
        // 1. ZSET 랭킹과 실시간 조회수 카운트는 캐시 히트 여부와 상관없이 '무조건' 올려줍니다.
        String redisKey = "post:viewCount:" + postId;
        redisTemplate.opsForValue().increment(redisKey);
        redisTemplate.opsForZSet().incrementScore(RANKING_KEY, String.valueOf(postId), 1);

        // 상세 조회를 위한 전용 캐시 키 (예: post:detail:15)
        String cacheKey = POST_DETAIL_CACHE_KEY_PREFIX + postId;

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

        // 3. Cache Miss: 캐시에 없으면 기존대로 DB에서 조회합니다.
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        PostResponse response = PostResponse.from(post);

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

        // ✨ [추가됨] 글이 수정되었으므로 기존 상세 캐시도 날려버립니다!
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

        // ✨ [추가됨] 글이 삭제되었으므로 상세 캐시도 날려버립니다!
        redisTemplate.delete(POST_DETAIL_CACHE_KEY_PREFIX + postId);
    }

//    // 글 목록 읽기.
//    public List<Post> getPostByBoard(Long boardId) {
//        List<Post> posts = postRepository.findByBoardIdOrderByIdDesc(boardId);
//        Post post = posts.get(0);
//        List<Comment> comments = post.getComments();
//        int commentsCount = comments.size();
//
//        return  postRepository.findByBoardIdOrderByIdDesc(boardId);
//    }

    // 글목록 읽기 수정본
//    @Transactional
//    public List<PostListResponse> getPostByBoard(Long boardId) {
//        List<Post> posts = postRepository.findByBoardIdOrderByIdDesc(boardId);
//
//        return posts.stream()
//                .map(p -> PostListResponse.from(p))
//                .toList();
//    }

    // 긁 읽기 페이징 버전 (✨ Read-Aside 캐싱 적용됨)
    public Page<PostListResponse> getPostByBoard(Long boardId, Pageable pageable) {
        // ✨ [추가됨] 특정 게시판의 특정 페이지에 대한 고유한 캐시 키를 만듭니다. (예: post:list:board:1:page:0)
        String cacheKey = "post:list:board:" + boardId + ":page:" + pageable.getPageNumber();

        try {
            // 1. Redis에서 데이터가 있는지 확인 (Cache Hit)
            String cachedJson = redisTemplate.opsForValue().get(cacheKey);
            if (cachedJson != null) {
//                log.info(">>> [Cache Hit] 게시판 {}번의 {}페이지를 Redis에서 가져옵니다.", boardId, pageable.getPageNumber());

                // 직렬화 에러를 막기 위해 Page 객체가 아닌 실제 데이터(List)만 캐싱하고 꺼냅니다.
                List<PostListResponse> content = objectMapper.readValue(cachedJson, new TypeReference<List<PostListResponse>>() {});

                // 꺼낸 데이터를 다시 PageImpl로 포장해서 기존 리턴 타입과 동일하게 맞춰줍니다.
                // (부하 테스트용으로 total 카운트를 10000으로 고정했습니다. 필요시 DB 카운트 쿼리 연동 가능)
                return new PageImpl<>(content, pageable, 10000);
            }
        } catch (JsonProcessingException e) {
            log.error("Redis 캐시 역직렬화 중 에러 발생: {}", e.getMessage());
        }

        // 2. Cache Miss: 기존 로직대로 DB에서 읽어옵니다.
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
        // 1. Redis ZSET에서 점수가 가장 높은 상위 10개의 Post ID를 가져옵니다. (내림차순)
        Set<String> topPostIds = redisTemplate.opsForZSet().reverseRange(RANKING_KEY, 0, 9);

        if (topPostIds == null || topPostIds.isEmpty()) {
            return Collections.emptyList();
        }

        // String 형태의 ID를 Long 형태로 변환
        List<Long> postIds = topPostIds.stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());

        // 2. DB에서 해당 ID들의 게시글 정보를 조회합니다.
        List<PostListResponse> posts = postRepository.findOptimizedByIds(postIds);

        // 3. DB의 IN 쿼리 결과는 순서가 보장되지 않으므로, Redis 랭킹 순서대로 다시 정렬합니다.
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