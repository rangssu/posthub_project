package com.posthub.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtUtil {

private static final String SECRET_STRING = "this-is-posthub-super-secret-key-must-be-very-long-32-bytes";
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET_STRING.getBytes(StandardCharsets.UTF_8));
    private static final long EXP_MILLIS = 1000L * 60 * 30; // 30분

    public static String generateToken(Long userId) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXP_MILLIS))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public static Long getUserId(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(KEY)
                .parseClaimsJws(token)
                .getBody();
        return Long.valueOf(claims.getSubject());
    }

    public static boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(KEY)
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return  false;
        }
    }


}
