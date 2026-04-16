package com.posthub.user.service;

import com.posthub.user.domain.User;
import com.posthub.user.dto.UserResponseDto;
import com.posthub.user.dto.UserUpdateRequestDto;
import com.posthub.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;


    @Transactional
    public Long createUser(User user) {
        // 아이디 및 닉네임 중복 여부 사전 검증
        if (userRepository.existsByLoginId(user.getLoginId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 아이디입니다.");
        }
        if (userRepository.existsByNickname(user.getNickname())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 닉네임입니다.");
        }

        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }

    @Transactional
    public UserResponseDto updateUser(Long id, UserUpdateRequestDto requestDto) {
        User user = findUserById(id);

        // 닉네임 변경 시, 기존 본인 닉네임과 다른 경우에만 중복 체크 수행
        if (requestDto.getNickname() != null && !requestDto.getNickname().equals(user.getNickname())) {
            if (userRepository.existsByNickname(requestDto.getNickname())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 닉네임입니다.");
            }
        }

        user.updateUser(
                requestDto.getPassword(),
                requestDto.getNickname()
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