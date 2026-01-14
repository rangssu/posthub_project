package com.posthub.post.domain;

import com.posthub.board.domain.Board;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
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

    public Post(String title, String content){
        this.title = title;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.viewCount = 0;
        this.board = getBoard();    // 이렇게 하면 SetBoard 하지 않고 board id를 가져올수 있겠지 ?
    }

    public void update(String title, String content){
        this.title = title;
        this.content = content;
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

}
