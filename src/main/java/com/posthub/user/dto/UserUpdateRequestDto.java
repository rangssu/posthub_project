package com.posthub.user.dto;

import lombok.Getter;

@Getter
public class UserUpdateRequestDto {

    private String password;
    private String name;
    private String nickname;

    public UserUpdateRequestDto() {
    }

    public UserUpdateRequestDto(String password, String name, String nickname) {
        this.password = password;
        this.name = name;
        this.nickname = nickname;
    }

}
