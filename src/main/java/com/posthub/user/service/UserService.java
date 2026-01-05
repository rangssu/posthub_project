package com.posthub.user.service;

import com.posthub.user.domain.User;
import com.posthub.user.dto.UserResponseDto;
import com.posthub.user.dto.UserUpdateRequestDto;
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

    @Transactional
    public UserResponseDto updateUser(Long id, UserUpdateRequestDto requestDto) {
        User user = findUserById(id);

        user.updateUser(
                requestDto.getPassword(),
                requestDto.getName(),
                requestDto.getNickname(),
                requestDto.getEmail()
        );

        return UserResponseDto.from(user);
    }

    public UserResponseDto getUser(Long id) {
        User user = findUserById(id);
        return UserResponseDto.from(user);
    }

    // 삭제
    @Transactional
    public void deleteUser(Long id) {
        User user = findUserById(id);
        userRepository.delete(user);
    }

    // id로 찾기
    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. id=" + id));
    }

}
