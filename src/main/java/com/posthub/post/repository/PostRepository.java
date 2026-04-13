package com.posthub.post.repository;

import com.posthub.post.domain.Post;
import com.posthub.post.dto.PostListResponse; // 👇 [추가] DTO 임포트
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // 👇 [추가] Param 임포트

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
//    Page<Post> findByBoardIdOrderByIdDesc(Long boardId, Pageable pageable);

    /**
     * @ EntityGraph는 쿼리 실행 시 연관된 엔티티를 한 번에 조인(Join)해서 가져오게 합니다.
     * 여기서는 Post를 조회할 때 작성자 정보인 'user'를 미리 패치 조인(Fetch Join)하여
     * PostListResponse로 변환할 때 발생하는 N+1 문제를 방지합니다.
     */
//    @EntityGraph(attributePaths = {"user"})
//    Page<Post> findByBoardIdOrderByIdDesc(Long boardId, Pageable pageable);

    // 👇 [추가됨] 불필요한 데이터(content 등)를 제외하고 필요한 컬럼만 딱 맞춰서 DTO로 즉시 변환하는 최적화 쿼리
    @Query(
            value = "SELECT new com.posthub.post.dto.PostListResponse(" +
                    "p.id, p.title, p.viewCount, p.createdAt, u.id, u.nickname, size(p.comments)) " +
                    "FROM Post p " +
                    "JOIN p.user u " +
                    "WHERE p.board.id = :boardId " +
                    "ORDER BY p.id DESC",
            countQuery = "SELECT count(p) FROM Post p WHERE p.board.id = :boardId"
    )
    Page<PostListResponse> findOptimizedByBoardId(@Param("boardId") Long boardId, Pageable pageable);

    @Modifying
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + :increaseCount WHERE p.id = :postId")
    void updateViewCount(@Param("postId") Long postId, @Param("increaseCount") Long increaseCount);

    // ✨ [실시간 인기글용 추가] 랭킹에 등록된 게시글 ID 여러 개를 한 번에 가져오는 최적화 쿼리
    @Query("SELECT new com.posthub.post.dto.PostListResponse(" +
            "p.id, p.title, p.viewCount, p.createdAt, u.id, u.nickname, size(p.comments)) " +
            "FROM Post p " +
            "JOIN p.user u " +
            "WHERE p.id IN :postIds")
    List<PostListResponse> findOptimizedByIds(@Param("postIds") List<Long> postIds);
}

// 👇 [수정됨] 작성자(user)와 댓글(comments)을 모두 fetch join으로 한 번에 가져옵니다.
//@Query(
//        value = "select distinct p from Post p " +
//                "join fetch p.user " +
//                "left join fetch p.comments " +
//                "where p.board.id = :boardId " +
//                "order by p.id desc",
//        countQuery = "select count(p) from Post p where p.board.id = :boardId"
//)

// 작성자(user) 패치 조인을 제거하고 댓글(comments)만 남긴 버전입니다.
//@Query(
//        value = "select distinct p from Post p " +
//                "left join fetch p.comments " +
//                "where p.board.id = :boardId " +
//                "order by p.id desc",
//        countQuery = "select count(p) from Post p where p.board.id = :boardId"
//)