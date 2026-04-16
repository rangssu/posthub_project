package com.posthub.post.dto;

import com.posthub.post.domain.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PostListResponse {
    private Long id;
    private String title;
    private int viewCount;
    private LocalDateTime createdAt;
    private Long userId;
    private String nickname;
    private int commentsSize;

    public PostListResponse(Long id, String title, int viewCount, LocalDateTime createdAt, Long userId, String nickname, int commentsSize) {
        this.id = id;
        this.title = title;
        this.viewCount = viewCount;
        this.createdAt = createdAt;
        this.userId = userId;
        this.nickname = nickname;
        this.commentsSize = commentsSize;
    }


    public static PostListResponse from(Post post) {
        return new PostListResponse(
                post.getId(),
                post.getTitle(),
                post.getViewCount(),
                post.getCreatedAt(),
                post.getUser().getId(),
                post.getUser().getNickname(),
                post.getComments().size()
        );
    }
}