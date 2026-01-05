package com.posthub.user.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")     // 없으니까 안됨 왜지 ?
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

    public void updateUser(String password, String name, String nickname){
        if (password != null && !password.isBlank()) {
            this.password = password;
        }
        if (name!=null && !name.isBlank() ) {
            this.name = name;
        }
        if (nickname != null && !nickname.isBlank() ) {
            this.nickname = nickname;
        }
//        if (email != null && !email.isBlank() ) {
//            this.email = email;
//        }     이메일은 수정할 필요가 없잖아 ? 아마두.




    }

}
