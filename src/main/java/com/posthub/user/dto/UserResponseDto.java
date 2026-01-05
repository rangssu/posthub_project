package com.posthub.user.dto;

import com.posthub.user.domain.User;
import lombok.Getter;

@Getter
public class UserResponseDto {
    private final Long id;
    private final String loginId;
    private final String name;
    private final String nickname;
    private final String email;

    public UserResponseDto(Long id, String loginId, String name, String nickname, String email){
        this.id = id;
        this.loginId = loginId;
        this.name = name;
        this.nickname = nickname;
        this.email = email;
    }

    public static UserResponseDto from(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getLoginId(),
                user.getName(),
                user.getNickname(),
                user.getEmail()
        );
    }

}
