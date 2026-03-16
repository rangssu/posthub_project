package com.posthub.post.repository;

import com.posthub.post.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByBoardIdOrderByIdDesc(Long boardId, Pageable pageable);

}
