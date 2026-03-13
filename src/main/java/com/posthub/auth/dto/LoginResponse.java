package com.posthub.auth.dto;

import lombok.Getter;

@Getter
public class LoginResponse {
    private String accessToken;
    private Long userId; // 👇 [추가]

    public LoginResponse(String accessToken, Long userId) { // 👇 파라미터 추가
        this.accessToken = accessToken;
        this.userId = userId; // 👇 [추가]
    }



}
