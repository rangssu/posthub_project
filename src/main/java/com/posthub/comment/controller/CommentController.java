package com.posthub.comment.controller;

import com.posthub.comment.dto.CommentRequest;
import com.posthub.comment.dto.CommentResponse;
import com.posthub.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {

    private  final CommentService commentService;

    // 댓글 생성.
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<Long> createComments(@AuthenticationPrincipal Long userId, @PathVariable Long postId, @RequestBody CommentRequest request) {
        Long commentId = commentService.createComments(userId, postId, request);

        return ResponseEntity.ok(commentId);
    }

    // 댁글 리스트.
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> commentsList(@PathVariable Long postId) {

        return ResponseEntity.ok(commentService.listByPost(postId));
    }

    // 수정
    @PutMapping("/comments/{commentsId}")
    public ResponseEntity<Long> editComments(@AuthenticationPrincipal Long userId , @PathVariable Long commentsId, @RequestBody CommentRequest request) {
        commentService.updateComments(userId, commentsId, request);
        return ResponseEntity.noContent().build();
    }


    //삭제
    @DeleteMapping("/comments/{commentsId}")
    public ResponseEntity<Void> deleteComments (@AuthenticationPrincipal Long userId, @PathVariable Long commentsId) {
        System.out.print("매핑 들어옴.");
        commentService.deleteComments(userId,commentsId);
        System.out.print("서비스 끝남");
        return ResponseEntity.noContent().build();
    }





}
