package com.posthub.post.domain;

import com.posthub.board.domain.Board;
import com.posthub.comment.domain.Comment;
import com.posthub.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
// board_id와 id DESC를 결합한 인덱스를 생성하여 최신순 페이징 조회 성능을 최적화함
@Table(name = "post", indexes = {
        @Index(name = "idx_board_id_id", columnList = "board_id, id DESC")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;
    private LocalDateTime createdAt;
    private int viewCount;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @OneToMany(mappedBy = "post",cascade = {CascadeType.REMOVE}, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    public Post(Board board, User user, String title, String content){
        this.board = board;
        this.user = user;
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
