package com.posthub.auth.service;

import com.posthub.auth.dto.LoginRequest;
import com.posthub.auth.dto.LoginResponse;
import com.posthub.auth.jwt.JwtUtil;
import com.posthub.user.domain.User;
import com.posthub.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthService {
    private final UserRepository userRepository;

    public  AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public LoginResponse login(LoginRequest req) {
        User user = userRepository.findByLoginId(req.getLoginId())
                .orElseThrow(()-> new RuntimeException("아이디가 존재하지 않습니다."));

        // 암호화 적용 전 평문 비교 로직 (PasswordConfig 적용 시 BCrypt 등으로 변경 필요)
        if (!user.getPassword().equals(req.getPassword())) {
            throw new RuntimeException("비밀번호가 옳지 않습니다.");
        }

        String token = JwtUtil.generateToken(user.getId());

        return new LoginResponse(token, user.getId());

    }

}
