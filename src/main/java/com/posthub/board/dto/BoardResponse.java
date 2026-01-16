package com.posthub.board.dto;

import com.posthub.board.domain.Board;
import lombok.Getter;

@Getter
public class BoardResponse {

    private Long id;
    private String boardName;

//    protected BoardResponse() {}

    public BoardResponse(Long id, String boardName) {
        this.id = id;
        this.boardName = boardName;
    }

    public static BoardResponse from(Board board) {
        return new BoardResponse(
                board.getId(),
                board.getBoardName()
            );
    }

}
