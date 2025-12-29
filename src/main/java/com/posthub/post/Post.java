package com.posthub.post;

import java.time.LocalDateTime;

public class Post {

    private String title;
    private String content;
    private LocalDateTime createdAt;
    private int viewCount;

    public Post(String title, String content){
        this.title = title;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.viewCount = 0;
    }
}
