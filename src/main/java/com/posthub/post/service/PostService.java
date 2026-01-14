package com.posthub.post.service;

import com.posthub.post.controller.dto.PostRequest;
import com.posthub.post.controller.dto.PostResponse;
import com.posthub.post.domain.Post;
import com.posthub.post.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)  //트랜잭션 노션 확인.
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    //Create 글작성
    @Transactional
    public Long create (String title, String content) {
        Post post = new Post(title, content);
        Post savedPost = postRepository.save(post);
        return savedPost.getId();
    }

    //Read 읽기
    public PostResponse getPost(Long id) {
        // 글 없을경우 예외
        Post post = postRepository.findById(id).orElseThrow(()-> new IllegalArgumentException("글 없음"));
        post.increaseViewCount(); //조회수
        return new PostResponse(post);
    }

    //Update 수정
    @Transactional
    public void update(Long id, PostRequest request) {
        Post post = postRepository.findById(id).orElseThrow(()-> new IllegalArgumentException("글을 찾을 수 없습니다."));
        post.update(request.getTitle(), request.getContent());
    }

    //Delete 삭제
    @Transactional
    public void delete(Long id) {
        postRepository.deleteById(id);
    }



}
