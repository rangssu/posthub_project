package com.posthub;

import com.posthub.board.domain.Board;
import com.posthub.board.repository.BoardRepository;
import com.posthub.comment.domain.Comment;
import com.posthub.comment.repository.CommentRepository;
import com.posthub.post.domain.Post;
import com.posthub.post.repository.PostRepository;
import com.posthub.user.domain.User;
import com.posthub.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class PosthubApplicationTests {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private BoardRepository boardRepository;
//
//    @Autowired
//    private PostRepository postRepository;
//
//    @Autowired
//    private CommentRepository commentRepository;
//
//    @Test
//    void contextLoads() {
//    }
//
//    // 1단계: Postman 테스트를 위해 DB에 1만 건의 데이터를 '실제로' 밀어 넣는 메서드
//    @Test
//    @Transactional
//    @Commit // 테스트 종료 후에도 DB에 데이터를 남겨두기 위해 반드시 필요합니다.
//    void generateDummyData() {
//        // 1. 유저 및 보드 생성 (게시글과 댓글의 필수 연관관계)
//        User user = new User("testuser", "1234", "테스터", "testNick", "test@test.com");
//        userRepository.save(user);
//
//        Board board = new Board("테스트게시판");
//        boardRepository.save(board);
//
//        // 2. 게시글 10,000건 생성
//        List<Post> posts = new ArrayList<>();
//        for (int i = 1; i <= 10000; i++) {
//            // Post 생성자에 맞게 board, user, title, content 순서로 전달합니다.
//            Post post = new Post(board, user, "테스트 게시글 " + i, "테스트 게시글 내용입니다. " + i);
//            posts.add(post);
//        }
//        // 반복문 안에서 매번 save()를 호출하는 것보다 saveAll()을 사용하는 것이 훨씬 빠릅니다.
//        postRepository.saveAll(posts);
//
//        // 3. 댓글 10,000건 생성 (각 게시글마다 1개씩 댓글 작성)
//        List<Comment> comments = new ArrayList<>();
//        for (int i = 0; i < posts.size(); i++) {
//            Post post = posts.get(i);
//            // Comment 생성자에 맞게 post, user, content 순서로 전달합니다.
//            Comment comment = new Comment(post, user, "테스트 댓글입니다. " + (i + 1));
//            comments.add(comment);
//        }
//        commentRepository.saveAll(comments);
//
//        System.out.println("============== 더미 데이터 10,000건 삽입 완료 ==============");
//        System.out.println("이제 PosthubApplication(서버)을 켜고 Postman으로 테스트하세요!");
//    }
//
//    // 2단계: 속도 테스트가 모두 끝난 후, DB를 다시 깨끗하게 비우는 메서드
//    @Test
//    @Transactional
//    @Commit // 삭제한 결과를 DB에 '실제로' 반영하기 위해 필요합니다.
//    void deleteDummyData() {
//        // 외래키(Foreign Key) 제약 조건 때문에 자식 테이블(댓글)부터 삭제해야 에러가 나지 않습니다.
//        commentRepository.deleteAllInBatch();
//        postRepository.deleteAllInBatch();
//        boardRepository.deleteAllInBatch();
//        userRepository.deleteAllInBatch();
//
//        System.out.println("============== 더미 데이터 모두 삭제 완료 ==============");
//    }
}