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
@Table(name = "users")     // 없으니까 안됨 왜지 ?
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

    public void updateUser(String password, String nickname){
        if (password != null && !password.isBlank()) {
            this.password = password;
        }
        if (nickname != null && !nickname.isBlank() ) {
            this.nickname = nickname;
        }
//        if (name!=null && !name.isBlank() ) {
//            this.name = name;
//        }
//        if (email != null && !email.isBlank() ) {
//            this.email = email;
//        }     이메일은 수정할 필요가 없잖아 ? 아마두.




    }

}