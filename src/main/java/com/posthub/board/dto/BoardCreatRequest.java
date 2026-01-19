package com.posthub.board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class BoardCreatRequest {

    @NotNull
    private String boardName;

    //protected  BoardCreatRequest() {}  //뭔지 모르겟음

    public String getBoardName() {
        return boardName;
    }
}
