package com.posthub.post.dto;

import com.posthub.post.domain.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostResponse {

    private final Long id;
    private final String title;
    private final String content;
    private final LocalDateTime createdAt;
    private final int viewCount;
    private final Long boardId;
// commentResponse 해서 List 로 보여주면 좋음.

    public PostResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.createdAt = post.getCreatedAt();
        this.boardId = post.getBoard().getId();
        this.viewCount = post.getViewCount();
    }



    public static PostResponse from(Post post) {
        return new PostResponse(post);
    }


}