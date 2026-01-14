package com.posthub.post.repository;

import com.posthub.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByBoardIdOderByIdDesc(Long boardId);

}
