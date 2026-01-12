package com.posthub.board.dto;

import com.posthub.board.domain.Board;
import lombok.Getter;

@Getter
public class BoardResponse {

    private Long id;
    private String tabName;

//    protected BoardResponse() {}

    public BoardResponse(Long id, String tabName) {
        this.id = id;
        this.tabName = tabName;
    }

    public static BoardResponse from(Board board) {
        return new BoardResponse(
                board.getId(),
                board.getTabName()
        );
    }

}
