package com.posthub.user.repository;

import com.posthub.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository <User, Long> {

    Optional<User> findByLoginId(String loginId);

    // 가입 및 수정 시 중복 검증을 위한 메서드
    boolean existsByLoginId(String loginId);
    boolean existsByNickname(String nickname);
}