package com.posthub.post.controller;

import com.posthub.post.controller.dto.PostCommand;
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

    @PostMapping
    public ResponseEntity<Long> createPost(@RequestBody PostCommand command){
        Long postId = postService.create(command.getTitle(), command.getTitle());

        return ResponseEntity.ok(postId);


    }



}
