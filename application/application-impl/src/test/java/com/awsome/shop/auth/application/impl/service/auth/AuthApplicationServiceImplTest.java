package com.awsome.shop.auth.application.impl.service.auth;

import com.awsome.shop.auth.application.api.dto.auth.TokenDTO;
import com.awsome.shop.auth.application.api.dto.auth.ValidateTokenDTO;
import com.awsome.shop.auth.application.api.dto.auth.request.LoginRequest;
import com.awsome.shop.auth.application.api.dto.auth.request.RegisterRequest;
import com.awsome.shop.auth.application.api.dto.auth.request.ValidateTokenRequest;
import com.awsome.shop.auth.application.api.dto.user.UserDTO;
import com.awsome.shop.auth.domain.model.user.Role;
import com.awsome.shop.auth.domain.model.user.UserEntity;
import com.awsome.shop.auth.domain.model.user.UserStatus;
import com.awsome.shop.auth.domain.service.auth.AuthDomainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthApplicationServiceImplTest {

    @Mock
    private AuthDomainService authDomainService;

    @InjectMocks
    private AuthApplicationServiceImpl authApplicationService;

    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUsername("testuser");
        userEntity.setName("测试用户");
        userEntity.setEmployeeId("EMP001");
        userEntity.setRole(Role.EMPLOYEE);
        userEntity.setStatus(UserStatus.ACTIVE);
    }

    // ==================== register ====================

    @Test
    void register_success_returnsUserDTO() {
        when(authDomainService.register("testuser", "pass123", "测试用户", "EMP001"))
                .thenReturn(userEntity);

        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("pass123");
        request.setName("测试用户");
        request.setEmployeeId("EMP001");

        UserDTO result = authApplicationService.register(request);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getRole()).isEqualTo("EMPLOYEE");
        assertThat(result.getStatus()).isEqualTo("ACTIVE");
    }

    // ==================== login ====================

    @Test
    void login_success_returnsTokenDTO() {
        when(authDomainService.login("admin", "admin123")).thenReturn("jwt-token");
        when(authDomainService.getTokenExpirationSeconds()).thenReturn(7200L);

        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("admin123");

        TokenDTO result = authApplicationService.login(request);

        assertThat(result.getToken()).isEqualTo("jwt-token");
        assertThat(result.getExpiresIn()).isEqualTo(7200L);
    }

    // ==================== validateToken ====================

    @Test
    void validateToken_validToken_returnsSuccess() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 1L);
        claims.put("role", "ADMIN");
        when(authDomainService.validateToken("good-token")).thenReturn(claims);

        ValidateTokenRequest request = new ValidateTokenRequest();
        request.setToken("good-token");

        ValidateTokenDTO result = authApplicationService.validateToken(request);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getOperatorId()).isEqualTo(1L);
        assertThat(result.getRole()).isEqualTo("ADMIN");
    }

    @Test
    void validateToken_invalidToken_returnsFailure() {
        when(authDomainService.validateToken("bad-token")).thenReturn(null);

        ValidateTokenRequest request = new ValidateTokenRequest();
        request.setToken("bad-token");

        ValidateTokenDTO result = authApplicationService.validateToken(request);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).contains("无效");
    }

    @Test
    void validateToken_missingUserId_returnsFailure() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", null);
        claims.put("role", "ADMIN");
        when(authDomainService.validateToken("incomplete-token")).thenReturn(claims);

        ValidateTokenRequest request = new ValidateTokenRequest();
        request.setToken("incomplete-token");

        ValidateTokenDTO result = authApplicationService.validateToken(request);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).contains("不完整");
    }

    @Test
    void validateToken_missingRole_returnsFailure() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 1L);
        claims.put("role", null);
        when(authDomainService.validateToken("no-role-token")).thenReturn(claims);

        ValidateTokenRequest request = new ValidateTokenRequest();
        request.setToken("no-role-token");

        ValidateTokenDTO result = authApplicationService.validateToken(request);

        assertThat(result.isSuccess()).isFalse();
    }
}
