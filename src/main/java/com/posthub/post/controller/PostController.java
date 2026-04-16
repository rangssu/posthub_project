package com.posthub.post.controller;

import com.posthub.post.dto.*;
import com.posthub.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 글작성
    @PostMapping("/board/{boardId}/posts")
    public ResponseEntity<Long> createPost(@PathVariable Long boardId, @AuthenticationPrincipal Long userId, @RequestBody PostRequest request){
        System.out.println("유저아이디 : " + userId);
        Long postId = postService.createPost(boardId, userId, request);

        return ResponseEntity.ok(postId);
    }

    // ✨ 실시간 인기글 TOP 10 조회
    // 반드시 상세 조회({postId}) 보다 위에 있어야 정상 동작합니다.
    @GetMapping("/posts/ranking")
    public ResponseEntity<List<PostListResponse>> getTrendingPosts() {
        return ResponseEntity.ok(postService.getTop10TrendingPosts());
    }

    // 읽기
    @GetMapping("/posts/{postId}") // ┌ 여기서 끌고 오는거임.
    public ResponseEntity<PostResponse> getPost(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPost(postId));
    }

    // 수정
    @PutMapping("/posts/{postId}")
    public ResponseEntity<Void> updatePost(@PathVariable Long postId, @AuthenticationPrincipal Long userId, @RequestBody PostUpdateRequest request) {
        postService.update(userId, postId, request);
        return ResponseEntity.noContent().build();
    }

    // 삭제
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal Long userId, @PathVariable Long postId) {
        System.out.println(userId);
        postService.delete(userId, postId);
        return ResponseEntity.noContent().build();
    }

    // 글목록 읽기
    @GetMapping("/boards/{boardId}/posts")
    public Page<PostListResponse> postList(@PathVariable Long boardId, @PageableDefault(size = 10) Pageable pageable) {
        return postService.getPostByBoard(boardId, pageable);
    }

}