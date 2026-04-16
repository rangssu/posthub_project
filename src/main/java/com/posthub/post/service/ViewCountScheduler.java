package com.posthub.post.service;

import com.posthub.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Redis에 임시 저장된 조회수를 주기적으로 DB에 동기화하는 스케줄러.
 * DB Write 부하를 줄이기 위해 Write-Back 전략을 응용함.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ViewCountScheduler {

    private final StringRedisTemplate redisTemplate;
    private final PostRepository postRepository;

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void syncViewCountFromRedis() {
        log.info(">>> 조회수 DB 동기화 스케줄러 시작");

        // "post:viewCount:*" 패턴의 모든 키를 탐색
        ScanOptions options = ScanOptions.scanOptions().match("post:viewCount:*").build();
        Cursor<String> cursor = redisTemplate.scan(options);

        int count = 0;
        while (cursor.hasNext()) {
            String key = cursor.next();
            try {
                String[] parts = key.split(":");
                Long postId = Long.parseLong(parts[2]);

                String value = redisTemplate.opsForValue().get(key);
                if (value != null) {
                    Long increaseCount = Long.parseLong(value);
                    postRepository.updateViewCount(postId, increaseCount);
                    redisTemplate.delete(key);
                    count++;
                }
            } catch (Exception e) {
                log.error("조회수 동기화 중 에러 발생 (key: {}): {}", key, e.getMessage());
            }
        }
        log.info(">>> 조회수 DB 동기화 완료 (총 {}개의 게시글 반영)", count);
    }
}