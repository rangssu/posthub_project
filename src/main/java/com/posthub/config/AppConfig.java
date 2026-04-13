package com.posthub.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; // ✨ 추가
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        // ✨ 핵심: Java 8 날짜/시간(LocalDateTime) 객체를 JSON으로 바꿀 수 있게 모듈 장착!
        objectMapper.registerModule(new JavaTimeModule());

        // (선택) 날짜를 숫자 배열이 아닌 예쁜 문자열(2026-04-10T15:26:30) 형태로 저장하게 설정
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return objectMapper;
    }
}