package com.posthub.board.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class BoardCreatRequest {

    @NotNull
    private String boardName;

    public String getBoardName() {
        return boardName;
    }
}
