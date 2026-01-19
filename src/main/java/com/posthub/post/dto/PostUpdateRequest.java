package com.posthub.post.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostUpdateRequest {
    private Long userId;
    private String title;
    private String content;
}
