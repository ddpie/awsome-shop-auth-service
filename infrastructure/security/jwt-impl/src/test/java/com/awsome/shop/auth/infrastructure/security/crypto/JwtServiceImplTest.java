package com.awsome.shop.auth.infrastructure.security.crypto;

import com.awsome.shop.auth.domain.model.user.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class JwtServiceImplTest {

    private JwtServiceImpl jwtService;

    // 至少 256 bits (32 bytes) 的密钥
    private static final String SECRET = "test-secret-key-must-be-at-least-256-bits-long-for-hmac-sha256";
    private static final long EXPIRATION = 7200;
    private static final String ISSUER = "test-auth-service";

    @BeforeEach
    void setUp() {
        jwtService = new JwtServiceImpl(SECRET, EXPIRATION, ISSUER);
    }

    // ==================== generateToken ====================

    @Test
    void generateToken_returnsNonEmptyString() {
        String token = jwtService.generateToken(1L, "admin", Role.ADMIN);

        assertThat(token).isNotBlank();
        // JWT 由三部分组成，以 . 分隔
        assertThat(token.split("\\.")).hasSize(3);
    }

    // ==================== validateToken ====================

    @Test
    void validateToken_validToken_returnsClaims() {
        String token = jwtService.generateToken(1L, "admin", Role.ADMIN);

        Map<String, Object> claims = jwtService.validateToken(token);

        assertThat(claims).isNotNull();
        assertThat(claims.get("userId")).isEqualTo(1L);
        assertThat(claims.get("username")).isEqualTo("admin");
        assertThat(claims.get("role")).isEqualTo("ADMIN");
    }

    @Test
    void validateToken_employeeRole() {
        String token = jwtService.generateToken(42L, "employee1", Role.EMPLOYEE);

        Map<String, Object> claims = jwtService.validateToken(token);

        assertThat(claims.get("userId")).isEqualTo(42L);
        assertThat(claims.get("role")).isEqualTo("EMPLOYEE");
    }

    @Test
    void validateToken_tamperedToken_returnsNull() {
        String token = jwtService.generateToken(1L, "admin", Role.ADMIN);
        // 篡改 token
        String tampered = token.substring(0, token.length() - 5) + "XXXXX";

        assertThat(jwtService.validateToken(tampered)).isNull();
    }

    @Test
    void validateToken_garbageString_returnsNull() {
        assertThat(jwtService.validateToken("not.a.jwt")).isNull();
    }

    @Test
    void validateToken_emptyString_returnsNull() {
        assertThat(jwtService.validateToken("")).isNull();
    }

    @Test
    void validateToken_differentSecret_returnsNull() {
        String token = jwtService.generateToken(1L, "admin", Role.ADMIN);

        // 用另一个密钥创建的 service 无法验证
        JwtServiceImpl otherService = new JwtServiceImpl(
                "another-secret-key-that-is-also-at-least-256-bits-long-for-hs256", EXPIRATION, ISSUER);

        assertThat(otherService.validateToken(token)).isNull();
    }

    @Test
    void validateToken_expiredToken_returnsNull() {
        // 过期时间设为 0 秒
        JwtServiceImpl expiredService = new JwtServiceImpl(SECRET, 0, ISSUER);
        String token = expiredService.generateToken(1L, "admin", Role.ADMIN);

        // token 立即过期
        assertThat(expiredService.validateToken(token)).isNull();
    }

    // ==================== getExpirationSeconds ====================

    @Test
    void getExpirationSeconds_returnsConfiguredValue() {
        assertThat(jwtService.getExpirationSeconds()).isEqualTo(7200);
    }
}
