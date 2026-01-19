package com.posthub.user.dto;

import lombok.Getter;

@Getter
public class LoginResponse {
    private Long userId;
    private String name;

    public LoginResponse(Long userid, String name) {
        this.userId = userid;
        this.name = name;
    }
}
