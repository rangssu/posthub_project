package com.posthub.post.controller.dto;

import com.posthub.board.domain.Board;
import com.posthub.post.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
public class PostResponse {

    private final Long id;
    private final String title;
    private final String content;
    private final LocalDateTime createdAt;
    private final int viewCount;
    private final Long boardId;


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