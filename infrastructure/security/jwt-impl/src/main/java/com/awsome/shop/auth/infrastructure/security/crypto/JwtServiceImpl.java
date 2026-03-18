package com.awsome.shop.auth.infrastructure.security.crypto;

import com.awsome.shop.auth.domain.model.user.Role;
import com.awsome.shop.auth.infrastructure.security.api.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 令牌服务实现（JJWT + HS256）
 */
@Slf4j
@Service
public class JwtServiceImpl implements JwtService {

    private final SecretKey secretKey;
    private final long expirationSeconds;
    private final String issuer;

    public JwtServiceImpl(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration}") long expirationSeconds,
            @Value("${security.jwt.issuer:awsome-shop-auth-service}") String issuer) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationSeconds = expirationSeconds;
        this.issuer = issuer;
    }

    @Override
    public String generateToken(Long userId, String username, Role role) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationSeconds * 1000);

        return Jwts.builder()
                .issuer(issuer)
                .subject(username)
                .claim("userId", userId)
                .claim("role", role.name())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    @Override
    public Map<String, Object> validateToken(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Map<String, Object> result = new HashMap<>();
            result.put("userId", claims.get("userId", Long.class));
            result.put("username", claims.getSubject());
            result.put("role", claims.get("role", String.class));
            return result;
        } catch (JwtException e) {
            log.warn("JWT 验证失败: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public long getExpirationSeconds() {
        return expirationSeconds;
    }
}
