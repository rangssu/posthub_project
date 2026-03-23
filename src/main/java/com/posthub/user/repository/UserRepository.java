package com.posthub.user.repository;

import com.posthub.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository <User, Long> {

    Optional<User> findByLoginId(String loginId);

    // 아이디 중복 체크
    boolean existsByLoginId(String loginId);

    // 닉네임 중복 체크
    boolean existsByNickname(String nickname);
}