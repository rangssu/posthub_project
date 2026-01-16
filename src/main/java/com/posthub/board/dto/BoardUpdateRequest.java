package com.posthub.board.dto;

import jakarta.validation.constraints.NotBlank;

public class BoardUpdateRequest {

    @NotBlank
    private String boardName;

    public String getBoardName() {
        return boardName;
    }
}
