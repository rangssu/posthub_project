package com.posthub.auth.dto;

import lombok.Getter;

@Getter
public class LoginResponse {
    private String accessToken;

    public LoginResponse(String accessToken) {
        this.accessToken = accessToken;
    }



}
