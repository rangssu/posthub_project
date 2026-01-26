package com.posthub.post.controller;

import com.posthub.post.dto.PostDeleteRequest;
import com.posthub.post.dto.PostRequest;
import com.posthub.post.dto.PostResponse;
import com.posthub.post.dto.PostUpdateRequest;
import com.posthub.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/board/{boardId}/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 글작성
    @PostMapping
    public ResponseEntity<Long> createPost(@PathVariable Long boardId, @AuthenticationPrincipal Long userId, @RequestBody PostRequest request){
        System.out.println("유저아이디 : " + userId);
        Long postId = postService.createPost(boardId, userId, request);

        return ResponseEntity.ok(postId);
    }

    // 읽기
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPost(postId));
    }

    // 수정
    @PutMapping("/{postId}")
    public ResponseEntity<Void> updatePost(@PathVariable Long boardId, @PathVariable Long postId, @AuthenticationPrincipal Long userId, @RequestBody PostUpdateRequest request) {
        postService.update(boardId, userId, postId, request);
        return ResponseEntity.noContent().build();
    }

    // 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal Long userId, @PathVariable Long postId) {
        System.out.println(userId);
        postService.delete(userId, postId);
        return ResponseEntity.noContent().build();
    }

    // 글목록ㅇㄹ기
    @GetMapping
    public List<PostResponse> postList(@PathVariable Long boardId) {
        return postService.getPostByBoard(boardId)
                .stream()
                .map(PostResponse::from)
                .toList();
    }

}
