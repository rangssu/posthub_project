package com.posthub.post.controller;

import com.posthub.post.dto.PostDeleteRequest;
import com.posthub.post.dto.PostRequest;
import com.posthub.post.dto.PostResponse;
import com.posthub.post.dto.PostUpdateRequest;
import com.posthub.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 글작성
    @PostMapping
    public ResponseEntity<Long> createPost(@PathVariable Long boardId, @RequestParam Long userId,@RequestBody PostRequest command){
        Long postId = postService.createPost(boardId, userId, command.getTitle(), command.getContent());

        return ResponseEntity.ok(postId);

    }

    // 읽기
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPost(id));
    }

    // 수정
    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePost(@PathVariable Long id, @RequestBody PostUpdateRequest request) {
        postService.update(id, request);
        return ResponseEntity.noContent().build();
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestBody PostDeleteRequest req) {
        postService.delete(id, req.getUserId());
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
