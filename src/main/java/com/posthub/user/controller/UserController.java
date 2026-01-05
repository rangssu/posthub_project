package com.posthub.user.controller;

import com.posthub.user.domain.User;
import com.posthub.user.dto.UserResponseDto;
import com.posthub.user.dto.UserUpdateRequestDto;
import com.posthub.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 아이디 생성
    @PostMapping
    public ResponseEntity<Long> createUser(@RequestBody User user) {
        Long userId = userService.createUser(user);
        return ResponseEntity.ok(userId);
    }

    //회원 정보 조회
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable Long id) {
        UserResponseDto user = userService.getUser(id);
        return ResponseEntity.ok(user);
    }

    // 회원 정보 수정
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable Long id,
            @RequestBody UserUpdateRequestDto requestDto
    ) {
        UserResponseDto updated = userService.updateUser(id, requestDto);
        return ResponseEntity.ok(updated);
    }


    // 회원탈퇴
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }



}
