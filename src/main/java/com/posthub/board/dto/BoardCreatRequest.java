package com.posthub.board.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class BoardCreatRequest {

    @NotBlank
    private String tabName;

    //protected  BoardCreatRequest() {}  //뭔지 모르겟음

    public String getTabName() {
        return tabName;
    }
}
