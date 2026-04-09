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
// 👇 [여기 추가됨!] board_id와 id DESC를 묶어 완벽한 커버링/복합 인덱스를 생성합니다.
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

/*
    public Post(String title, String content){
        this.title = title;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.viewCount = 0;
        this.board = getBoard();    // 이렇게 하면 SetBoard 하지 않고 board id를 가져올수 있겠지 ?
    }
*/
    public Post(Board board, User user, String title, String content){
        this.board = board;
        this.user = user;
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
