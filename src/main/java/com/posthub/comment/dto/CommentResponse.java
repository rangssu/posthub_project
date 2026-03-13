package com.posthub.comment.dto;

import com.posthub.comment.domain.Comment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponse {

    private final Long commentId;
    private final Long userId;
    private final String content;
    private final LocalDateTime createAt;
    private final String nickname;

    public CommentResponse(Long commentId, Long userId, String content, LocalDateTime createAt, String nickname) {
        this.commentId = commentId;
        this.userId = userId;
        this.content = content;
        this.createAt = createAt;
        this.nickname = nickname;
    }

    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getUser().getId(),
                comment.getContent(),
                comment.getCreateAt(),
                comment.getUser().getNickname()
        );
    }

}
