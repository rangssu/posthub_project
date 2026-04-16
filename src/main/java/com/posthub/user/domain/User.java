package com.posthub.user.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")     /// 관례에 따른 테이블명 명시
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 아이디
    @NotBlank(message = "아이디는 공백일 수 없습니다.")
    @Size(min = 4, max = 20, message = "아이디는 4자 이상 20자 이하여야 합니다.")
    @Column(nullable = false, unique = true)
    private String loginId;

    // 비밀번호
    @NotBlank(message = "비밀번호는 공백일 수 없습니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하여야 합니다.")
    @Column(nullable = false)
    private String password;

    //이름
    @NotBlank(message = "이름은 공백일 수 없습니다.")
    @Column(nullable = false)
    private String name;

    //닉네임
    @NotBlank(message = "닉네임은 공백일 수 없습니다.")
    @Column(nullable = false, unique = true)
    private String nickname;

    //이메일
    @NotBlank(message = "이메일은 공백일 수 없습니다.")
    @Column(nullable = false, unique = true)
    private String email;

    public User(String loginId, String password, String name, String nickname, String email) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.email = email;
    }

    /**
     * 회원 정보 수정 (비밀번호, 닉네임)
     * 변경 가능한 필드에 대해서만 선택적으로 업데이트 수행
     */
    public void updateUser(String password, String nickname){
        if (password != null && !password.isBlank()) {
            this.password = password;
        }
        if (nickname != null && !nickname.isBlank() ) {
            this.nickname = nickname;
        }

    }

}