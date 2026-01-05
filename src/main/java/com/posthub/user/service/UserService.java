package com.posthub.user.service;

import com.posthub.user.domain.User;
import com.posthub.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    // 메서드 생성시 이름으로 기능을 알 수 있게 생성하기.
    @Transactional
    public Long createUser(User user) {
        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }

}
