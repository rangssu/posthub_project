package com.posthub.post.service;

import com.posthub.post.domain.Post;
import com.posthub.post.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional  //트랜잭션 노션 확인.
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Long create (String title, String content) {
        Post post = new Post(title, content);
        Post savedPost = postRepository.save(post);
        return savedPost.getId();
    }

}
