package com.posthub.auth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT 기반 인증 필터
 * - 모든 요청마다 실행됨
 * - Authorization 헤더에서 JWT를 꺼내 인증 처리
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * 요청이 들어올 때마다 자동으로 호출되는 메서드
     * (Spring Security 필터 체인에 등록되면)
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {



        // Authorization 헤더에서 Bearer 토큰 추출
        String token = resolveToken(request);

//        System.out.println("TOKEN = " + token);
//        System.out.println("VALID = " + (token != null && JwtUtil.validateToken(token)));
        if (token != null) System.out.println("USER_ID = " + JwtUtil.getUserId(token));

        /**
         *  토큰이 존재하고 + 유효하다면
         * → "이 요청은 인증된 사용자 요청이다" 라고 Security에 알려줘야 한다
         */
        if (token != null && JwtUtil.validateToken(token)) {

            //  토큰 안에서 userId 추출
            Long userId = JwtUtil.getUserId(token);

            /**
             *  Authentication 객체 생성
             *
             * principal  : 인증된 사용자 정보 (여기서는 userId)
             * credentials : 비밀번호 (JWT 방식이라 null)
             * authorities : 권한 목록 (아직 Role 안 쓰므로 비움)
             */
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            userId,
                            null,
                            Collections.emptyList()
                    );

            /**
             *  요청 정보(IP, 세션 등)를 Authentication에 부가 정보로 세팅
             * (없어도 되지만, Security 표준 흐름)
             */
            ((UsernamePasswordAuthenticationToken) authentication)
                    .setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

            /**
             *  ★가장 중요★
             * SecurityContext에 인증 정보 저장
             *
             * → 이 순간부터 Spring Security는
             *   "이 요청은 인증된 사용자 요청"이라고 인식함
             */
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        //  다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    /**
     * Authorization 헤더에서 Bearer 토큰만 잘라서 반환
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 제거
        }
        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        // /health 경로로 들어오는 요청은 이 필터를 무시하고 통과시킴
        if (path.equals("/health")) {
            return true;
        }

        // 필요하다면 로그인이나 회원가입 경로도 여기에 추가할 수 있습니다.
        // if (path.startsWith("/api/auth/login") || path.equals("/api/users")) {
        //     return true;
        // }

        return false;
    }
}
