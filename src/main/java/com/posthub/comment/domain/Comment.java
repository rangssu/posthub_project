package com.posthub.comment.domain;

import com.posthub.post.domain.Post;
import com.posthub.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private LocalDateTime createAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "postId", nullable = false)
    private Post post;

    public Comment(Post postId, User userId, String content) {
        this.post = postId;
        this.user = userId;
        this.content = content;
        this.createAt = LocalDateTime.now();
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public boolean validateAuthor(Long userId) {
        return user.getId() != null && user.getId().equals(userId);
    }
}
