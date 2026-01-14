package com.posthub.board.controller;

import com.posthub.board.dto.BoardCreatRequest;
import com.posthub.board.dto.BoardResponse;
import com.posthub.board.dto.BoardUpdateRequest;
import com.posthub.board.service.BoardService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/boards")
public class BoardController {

    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    // 탭 목록
    @GetMapping
    public List<BoardResponse> list() {
        return boardService.findAll()
                .stream()
                .map(BoardResponse::from)
                .toList();
    }

    // 탭 단건 조회
    @GetMapping("/{boardId}")
    public BoardResponse get(@PathVariable Long boardId) {
        return BoardResponse.from(boardService.findById(boardId));
    }

    // 탭 생성
    @PostMapping
    public BoardResponse create(@RequestBody @Valid BoardCreatRequest req) {
        return BoardResponse.from(boardService.boardCreate(req));
    }

    // 탭 이름 수정
    @PutMapping("/{boardId}")
    public BoardResponse update(  @PathVariable Long boardId, @RequestBody @Valid BoardUpdateRequest req ) {
        return BoardResponse.from(boardService.boardUpdate(boardId, req));
    }

    // 탭 삭제
    @DeleteMapping("/{boardId}")
    public void delete(@PathVariable Long boardId) {
        boardService.delete(boardId);
    }
}
