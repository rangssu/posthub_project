package com.posthub.board.service;

import com.posthub.board.domain.Board;
import com.posthub.board.dto.BoardCreatRequest;
import com.posthub.board.dto.BoardUpdateRequest;
import com.posthub.board.repository.BoardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @Transactional
    public List<Board> findAll() {
        return boardRepository.findAll();
    }

    @Transactional
    public Board findById(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Board not found: " + boardId));
    }

    // 생성
    public Board boardCreate(BoardCreatRequest req) {
        Board board = new Board();
        return boardRepository.save(board);
    }

    public Board boardUpdate(Long boardId, BoardUpdateRequest req) {
        Board board = findById(boardId);
        board.editTabName(req.getTabName());
        return board;
    }


    public void delete(Long boardId) {
        Board board = findById(boardId);
        boardRepository.delete(board);
    }
}
