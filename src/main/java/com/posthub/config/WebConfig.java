package com.posthub.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 우리 백엔드의 모든 API 주소에 대해서
                .allowedOrigins("http://localhost:5173") // 리액트(5173 포트)의 접근을 허락해줍니다.
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 요청 방식들
                .allowedHeaders("*")
                .allowCredentials(true); // 인증 정보(토큰 등)를 포함한 요청도 허락합니다.
    }
}