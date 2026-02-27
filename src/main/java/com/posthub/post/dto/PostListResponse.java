package com.posthub.post.dto;

import com.posthub.post.domain.Post;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class PostListResponse {
    private Long id;
    private String title;
    private int viewCount;
    private LocalDateTime createAt;
    private String userName;
    private int commentsSize;

    public PostListResponse(Long id, String title, int viewCount, LocalDateTime createAt, String userName, int commentsSize) {
        this.id = id;
        this.title = title;
        this.viewCount = viewCount;
        this.createAt = createAt;
        this.userName = userName;
        this.commentsSize = commentsSize;
    }


    //dto
//    public List<PostListResponse> from(List<Post> posts) {
//
//
//        List<PostListResponse> responseList = new ArrayList<>();
//        responseList = posts.stream()
//                .map(p -> from(p))
//                .toList();
//
//        //List<PostListResponse> responseList = new ArrayList<>();
//
////        for (int i = 0 ; i<posts.size() ; i++) {
////            Post post = posts.get(i);
////            PostListResponse response = from(post);
////
////            responseList.add(response);
////        }
//        return responseList;
//    }

    public static PostListResponse from(Post post) {
        return new PostListResponse(
                post.getId(),
                post.getTitle(),
                post.getViewCount(),
                post.getCreatedAt(),
                post.getUser().getName(),
                post.getComments().size()
        );
    }

/*
//        Post post = posts.get(0);
//        PostListResponse response = new PostListResponse(post.getId(),
//                post.getTitle(),
//                post.getViewCount(),
//                post.getCreatedAt(),
//                post.getUser().getName(),
//                post.getComments().size()
//        );

        List<PostListResponse> responseList = new ArrayList<>();

        // posts -> ? -> ? -> List<PostListResponse>
        // 서랍안에 음색재료가 있음 -> ? -> ? -> 요리완성
        // posts -> 도마로 갖고옴. -> 손질함(변환 ? )  -> 결과적으로 posts 를 list<~>  에 담음.

        // for ~~ posts.siez
        for (Post post : posts) {
            PostListResponse response = new PostListResponse(post.getId(),
                    post.getTitle(),
                    post.getViewCount(),
                    post.getCreatedAt(),
                    post.getUser().getName(),
                    post.getComments().size()
            );
            responseList.add(response);
        }

        for (int i = 0 ; i<posts.size() ; i++) {
            Post post = posts.get(i);
            PostListResponse response = new PostListResponse(post.getId(),
                    post.getTitle(),
                    post.getViewCount(),
                    post.getCreatedAt(),
                    post.getUser().getName(),
                    post.getComments().size()
            );
            responseList.add(response);
        }


//        Stream<Post> postStream =

        // 22
        for (int i = 0 ; i<posts.size() ; i++) {
            Post post = posts.get(i);
            PostListResponse response = from(post);

            responseList.add(response);
        }
        // 22
        for (Post post : posts) {
            PostListResponse response = from(post);
            responseList.add(response);
        }


 */


}
