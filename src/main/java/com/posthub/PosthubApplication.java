package com.posthub;

import com.posthub.post.domain.Post;
import com.posthub.post.repository.PostRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PosthubApplication {

    public static void main(String[] args) {
        SpringApplication.run(PosthubApplication.class, args);
    }

}

