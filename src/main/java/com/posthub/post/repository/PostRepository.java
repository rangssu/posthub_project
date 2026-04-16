package com.posthub.post.repository;

import com.posthub.post.domain.Post;
import com.posthub.post.dto.PostListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {


    /**
     * N+1 문제를 방지하고 메모리 부하를 줄이기 위해
     * 엔티티 대신 필요한 필드만 선택하여 DTO로 즉시 프로젝션함
     */
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

    // 실시간 인기글 조회를 위해 여러 ID를 한 번에 조회하는 최적화 쿼리
    @Query("SELECT new com.posthub.post.dto.PostListResponse(" +
            "p.id, p.title, p.viewCount, p.createdAt, u.id, u.nickname, size(p.comments)) " +
            "FROM Post p " +
            "JOIN p.user u " +
            "WHERE p.id IN :postIds")
    List<PostListResponse> findOptimizedByIds(@Param("postIds") List<Long> postIds);
}