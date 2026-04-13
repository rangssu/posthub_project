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
import java.util.List;

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

    //Read 읽기
    @Transactional
    public PostResponse getPost(Long postId) {
        // 1. DB에서 게시글을 찾아옵니다. (이때 조회수 0)
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        // 2. 조회수를 1 올립니다. (이때 메모리 안에서만 1)
//        post.increaseViewCount();
        // 2. redis 로 수정
        String redisKey = "post:viewCount:" + postId;
        redisTemplate.opsForValue().increment(redisKey);

        // 3. @Transactional이 붙어있기 때문에, 이 메서드가 끝날 때 Spring이 알아서 바뀐 값(1)을 DB에 UPDATE 해줍니다.
        return PostResponse.from(post);
    }

    //Update 수정
    @Transactional
    public void update(Long userId, Long postId, PostUpdateRequest request) {
        Post post = getPostOrThrow(postId);
        validationAuthor(post, userId);
        post.update(request.getTitle(), request.getContent());

        // ✨ [추가됨] 글이 수정되었으므로 목록 캐시를 비워 최신화합니다.
        clearBoardPostCache(post.getBoard().getId());
    }

    //Delete 삭제
    @Transactional
    public void delete(Long userId, Long postId) {
        Post post = getPostOrThrow(postId);
        validationAuthor(post, userId);

        // ✨ [추가됨] 삭제 전에 어느 게시판인지 ID를 기억해둡니다.
        Long boardId = post.getBoard().getId();

        postRepository.deleteById(postId);

        // ✨ [추가됨] 삭제되었으므로 목록 캐시를 비워 최신화합니다.
        clearBoardPostCache(boardId);
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