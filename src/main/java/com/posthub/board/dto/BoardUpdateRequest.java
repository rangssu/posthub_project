package com.posthub.board.dto;

import jakarta.validation.constraints.NotBlank;

public class BoardUpdateRequest {

    @NotBlank
    private String tabName;

    public String getTabName() {
        return tabName;
    }
}
