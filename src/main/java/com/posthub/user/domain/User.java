package com.posthub.user.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 아이디
    @Column(nullable = false, unique = true)
    private String loginId;

    // 비밀번호
    @Column(nullable = false)
    private String password;

    //이름
    @Column(nullable = false)
    private String name;

    //닉네임
    @Column(nullable = false, unique = true)
    private String nickname;

    //이메일
    @Column(nullable = false, unique = true)
    private String email;

    public User(String loginId, String password, String name, String nickname, String email) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.email = email;
    }

}
