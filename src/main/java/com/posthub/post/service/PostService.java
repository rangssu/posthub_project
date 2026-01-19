package com.posthub.post.service;

import com.posthub.board.domain.Board;
import com.posthub.board.repository.BoardRepository;
import com.posthub.common.exception.FobiddenException;
import com.posthub.common.exception.NotFoundException;
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
    public Long createPost (Long boardId, Long userId, String title, String content) {

        Board board = boardRepository.findById(boardId)
                .orElseThrow(()-> new IllegalArgumentException("보드 번호를 찾을수 없습니다 + " + boardId));
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException(("user not found + " + userId)));

        Post post = new Post(board, user, title, content);
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
    public void update(Long id, PostUpdateRequest request) {
        Post post = getPostOrThrow(id);
        validationAuthor(post, request.getUserId());
        post.update(request.getTitle(), request.getContent());
    }

    //Delete 삭제
    @Transactional
    public void delete(Long postId, Long requesterUserId) {
        Post post = getPostOrThrow(postId);
        validationAuthor(post, requesterUserId);

        postRepository.delete(post);
    }

    // 글 목록 읽기.
    public List<Post> getPostByBoard(Long boardId) {
        return  postRepository.findByBoardIdOrderByIdDesc(boardId);
    }

    @Transactional(readOnly = true)
    public Post getPostOrThrow(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(()-> new NotFoundException("게시글을 찾을 수 없슴니다."));
    }
    // 작성자 확인 검증
    private void validationAuthor (Post post, Long requesterUserId) {
        if (requesterUserId == null) {
            throw new IllegalArgumentException("user_id 가 없습니다.");
        }
        if (!post.getUser().equals(requesterUserId)){
            throw new FobiddenException("작성자만 수정/삭제 할 수있습니다.");
        }
    }

}
