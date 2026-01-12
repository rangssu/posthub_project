package com.posthub.user.dto;

import lombok.Getter;

@Getter
public class UserUpdateRequestDto {

    private String password;
    private String nickname;

    public UserUpdateRequestDto() {
    }

    public UserUpdateRequestDto(String password, String nickname) {
        this.password = password;
        this.nickname = nickname;
    }

}
