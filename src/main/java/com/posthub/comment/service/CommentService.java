package com.posthub.comment.service;

import com.posthub.comment.domain.Comment;
import com.posthub.comment.dto.CommentRequest;
import com.posthub.comment.dto.CommentResponse;
import com.posthub.comment.repository.CommentRepository;
import com.posthub.post.domain.Post;
import com.posthub.post.repository.PostRepository;
import com.posthub.user.domain.User;
import com.posthub.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    //글작성
    @Transactional
    public Long createComments(Long userId, Long postId, CommentRequest request) {
        User user = getUserOrThrow(userId);
        Post post = getPostOrThrow(postId);

        Comment comment = new Comment(post, user, request.getContent());
        return commentRepository.save(comment).getId();
    }

    @Transactional
    public List<CommentResponse> listByPost(Long postId) {
        return commentRepository.findByPost_IdOrderByIdDesc(postId)
                .stream()
                .map(CommentResponse::from)
                .toList();
    }

    @Transactional
    public void updateComments(Long userId, Long commentId, CommentRequest request ) {
        Comment comment = getCommentOrThrow(commentId);
        validateAuthorOrThrow(comment, userId);

        comment.updateContent(request.getContent());
    }

    @Transactional
    public void deleteComments(Long userId, Long commentId) {
        System.out.print("서비스 들어옴");
        Comment comment = getCommentOrThrow(commentId);
        System.out.print("2번 지남");
        validateAuthorOrThrow(comment, userId);
        System.out.print("3번 지남.");
        commentRepository.deleteById(commentId);
    }














    /* 여기 아래는 오류임 */
    private Comment getCommentOrThrow(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
    }
    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("유저를 찾을 수 없습니다."));
    }
    private Post getPostOrThrow(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(()-> new IllegalArgumentException("게시물을 찾을 수 없습니다."));
    }
    private void validateAuthorOrThrow(Comment comment, Long userId) {
        if (!comment.validateAuthor(userId)) {
            throw new IllegalArgumentException("작성자만 가능합니다.");
        }
    }

}
