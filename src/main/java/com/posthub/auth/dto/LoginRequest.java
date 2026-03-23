package com.posthub.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class LoginRequest {
    @NotBlank(message = "아이디를 입력해주세요.")
    @Size(min = 4, max = 20, message = "아이디는 4자 이상 20자 이하여야 합니다.")
    private String loginId;
    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하여야 합니다.")
    private String password;

    public LoginRequest(String loginId, String password) {
        this.loginId = loginId;
        this.password = password;
    }
}
