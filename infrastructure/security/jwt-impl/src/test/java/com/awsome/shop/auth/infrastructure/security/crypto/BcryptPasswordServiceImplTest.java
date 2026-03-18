package com.awsome.shop.auth.infrastructure.security.crypto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class BcryptPasswordServiceImplTest {

    private BcryptPasswordServiceImpl passwordService;

    @BeforeEach
    void setUp() {
        passwordService = new BcryptPasswordServiceImpl();
    }

    @Test
    void encode_returnsNonNullBcryptHash() {
        String hash = passwordService.encode("admin123");

        assertThat(hash).isNotBlank();
        assertThat(hash).startsWith("$2a$");
    }

    @Test
    void encode_samePasswordProducesDifferentHashes() {
        // bcrypt 每次生成不同的 salt
        String hash1 = passwordService.encode("password");
        String hash2 = passwordService.encode("password");

        assertThat(hash1).isNotEqualTo(hash2);
    }

    @Test
    void matches_correctPassword_returnsTrue() {
        String hash = passwordService.encode("admin123");

        assertThat(passwordService.matches("admin123", hash)).isTrue();
    }

    @Test
    void matches_wrongPassword_returnsFalse() {
        String hash = passwordService.encode("admin123");

        assertThat(passwordService.matches("wrong", hash)).isFalse();
    }

    @Test
    void matches_caseSensitive() {
        String hash = passwordService.encode("Admin123");

        assertThat(passwordService.matches("admin123", hash)).isFalse();
    }
}
