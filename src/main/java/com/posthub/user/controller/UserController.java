package com.posthub.user.controller;

import com.posthub.user.domain.User;
import com.posthub.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/useres")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 아이디 생성
    @PostMapping
    public ResponseEntity<Long> createUser(@RequestBody User user) {
        Long userId = userService.createUser(user);
        return ResponseEntity.ok(userId);
    }




}
