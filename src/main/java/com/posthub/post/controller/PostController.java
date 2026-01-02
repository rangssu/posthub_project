package com.posthub.post.controller;

import com.posthub.post.controller.dto.PostRequest;
import com.posthub.post.controller.dto.PostResponse;
import com.posthub.post.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // 글작성
    @PostMapping
    public ResponseEntity<Long> createPost(@RequestBody PostRequest command){
        Long postId = postService.create(command.getTitle(), command.getTitle());

        return ResponseEntity.ok(postId);

    }

    // 읽기
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(postService.get(id));
    }

    // 수정
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody PostRequest request) {
        postService.update(id, request);
        return ResponseEntity.ok().build();
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        postService.delete(id);
        return ResponseEntity.ok().build();
    }
}



}
