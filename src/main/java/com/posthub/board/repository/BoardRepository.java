package com.posthub.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.posthub.board.domain.Board;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
