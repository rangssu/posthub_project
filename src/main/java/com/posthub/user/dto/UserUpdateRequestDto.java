package com.posthub.user.dto;

public class UserUpdateRequestDto {

    private String password;
    private String name;
    private String nickname;
    private String email;

    public UserUpdateRequestDto() {
    }

    public UserUpdateRequestDto(String password,
                                String name,
                                String nickname,
                                String email) {
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }
}
