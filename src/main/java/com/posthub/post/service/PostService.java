package com.posthub.post.service;

import com.posthub.board.domain.Board;
import com.posthub.board.repository.BoardRepository;
import com.posthub.comment.domain.Comment;
import com.posthub.common.exception.FobiddenException;
import com.posthub.common.exception.NotFoundException;
import com.posthub.post.dto.PostListResponse;
import com.posthub.post.dto.PostRequest;
import com.posthub.post.dto.PostResponse;
import com.posthub.post.domain.Post;
import com.posthub.post.dto.PostUpdateRequest;
import com.posthub.post.repository.PostRepository;
import com.posthub.user.domain.User;
import com.posthub.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)  //트랜잭션 노션 확인.
public class PostService {

    private final PostRepository postRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository,
                       BoardRepository boardRepsitory,
                       UserRepository userRepository) {
        this.postRepository = postRepository;
        this.boardRepository = boardRepsitory;
        this.userRepository = userRepository;
    }

    //Create 글작성
    @Transactional
    public Long createPost (Long boardId, Long userId, PostRequest request) {

        Board board = boardRepository.findById(boardId)
                .orElseThrow(()-> new IllegalArgumentException("보드 번호를 찾을수 없습니다 + " + boardId));
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException(("user not found + " + userId)));

        Post post = new Post(board, user, request.getTitle(), request.getContent());
        Post savedPost = postRepository.save(post);
        return savedPost.getId();
    }

    //Read 읽기
    public PostResponse getPost(Long id) {
        // 글 없을경우 예외
        Post post = postRepository.findById(id).orElseThrow(()-> new IllegalArgumentException("글 없음"));
        post.increaseViewCount(); //조회수
        return new PostResponse(post);
    }

    //Update 수정
    @Transactional
    public void update(Long boardId, Long userId, Long postId, PostUpdateRequest request) {
        Post post = getPostOrThrow(postId);
        if (!post.getBoard().getId().equals(boardId)) {
            throw new NotFoundException("해당 게시글은 이 게시판에 속하지 않습니다.");
        }
        validationAuthor(post, userId);
        post.update(request.getTitle(), request.getContent());
    }

    //Delete 삭제
    @Transactional
    public void delete(Long userId, Long postId) {
        Post post = getPostOrThrow(postId);
        validationAuthor(post, userId);

        postRepository.deleteById(postId);
    }

    // 글 목록 읽기.
    public List<Post> getPostByBoard(Long boardId) {
        List<Post> posts = postRepository.findByBoardIdOrderByIdDesc(boardId);
        Post post = posts.get(0);
        List<Comment> comments = post.getComments();
        int commentsCount = comments.size();

        return  postRepository.findByBoardIdOrderByIdDesc(boardId);
    }

    public List<PostListResponse> getPostByBoardV2(Long boardId) {
        List<Post> posts = postRepository.findByBoardIdOrderByIdDesc(boardId);

        return posts.stream()
                .map(p -> PostListResponse.from(p))
                .toList();
    }

    @Transactional(readOnly = true)
    public Post getPostOrThrow(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(()-> new NotFoundException("게시글을 찾을 수 없슴니다."));
    }
    // 작성자 확인 검증
    private void validationAuthor (Post post, Long requesterUserId) {
        if (!post.getUser().getId().equals(requesterUserId)) {
            throw new FobiddenException("작성자만 수정/삭제 할 수있습니다.");
        }
    }

}
