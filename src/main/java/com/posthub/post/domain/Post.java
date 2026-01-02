package com.posthub.post.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;
    private LocalDateTime createdAt;
    private int viewCount;

    public Post() {
    }

    public Post(String title, String content){
        this.title = title;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.viewCount = 0;
    }

    public void update(String title, String content){
        this.title = title;
        this.content = content;
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

}
