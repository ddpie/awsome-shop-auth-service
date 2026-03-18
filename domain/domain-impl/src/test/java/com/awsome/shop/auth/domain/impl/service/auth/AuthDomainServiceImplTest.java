package com.awsome.shop.auth.domain.impl.service.auth;

import com.awsome.shop.auth.common.exception.BusinessException;
import com.awsome.shop.auth.domain.model.user.Role;
import com.awsome.shop.auth.domain.model.user.UserEntity;
import com.awsome.shop.auth.domain.model.user.UserStatus;
import com.awsome.shop.auth.infrastructure.security.api.service.JwtService;
import com.awsome.shop.auth.infrastructure.security.api.service.PasswordService;
import com.awsome.shop.auth.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthDomainServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordService passwordService;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthDomainServiceImpl authDomainService;

    private UserEntity activeAdmin;

    @BeforeEach
    void setUp() {
        activeAdmin = new UserEntity();
        activeAdmin.setId(1L);
        activeAdmin.setUsername("admin");
        activeAdmin.setPassword("$2a$10$hashedPassword");
        activeAdmin.setName("管理员");
        activeAdmin.setEmployeeId("EMP000");
        activeAdmin.setRole(Role.ADMIN);
        activeAdmin.setStatus(UserStatus.ACTIVE);
    }

    // ==================== register ====================

    @Test
    void register_success() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmployeeId("EMP001")).thenReturn(false);
        when(passwordService.encode("password123")).thenReturn("$2a$10$encoded");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity u = invocation.getArgument(0);
            u.setId(2L);
            return u;
        });

        UserEntity result = authDomainService.register("newuser", "password123", "新用户", "EMP001");

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getUsername()).isEqualTo("newuser");
        assertThat(result.getPassword()).isEqualTo("$2a$10$encoded");
        assertThat(result.getRole()).isEqualTo(Role.EMPLOYEE);
        assertThat(result.getStatus()).isEqualTo(UserStatus.ACTIVE);
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void register_duplicateUsername_throwsConflict001() {
        when(userRepository.existsByUsername("admin")).thenReturn(true);

        assertThatThrownBy(() -> authDomainService.register("admin", "pass", "name", "EMP001"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode").isEqualTo("CONFLICT_001");
    }

    @Test
    void register_duplicateEmployeeId_throwsConflict002() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmployeeId("EMP000")).thenReturn(true);

        assertThatThrownBy(() -> authDomainService.register("newuser", "pass", "name", "EMP000"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode").isEqualTo("CONFLICT_002");
    }

    // ==================== login ====================

    @Test
    void login_success() {
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(activeAdmin));
        when(passwordService.matches("admin123", "$2a$10$hashedPassword")).thenReturn(true);
        when(jwtService.generateToken(1L, "admin", Role.ADMIN)).thenReturn("jwt-token");

        String token = authDomainService.login("admin", "admin123");

        assertThat(token).isEqualTo("jwt-token");
    }

    @Test
    void login_userNotFound_throwsAuth001() {
        when(userRepository.findByUsername("nobody")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authDomainService.login("nobody", "pass"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode").isEqualTo("AUTH_001");
    }

    @Test
    void login_accountDisabled_throwsForbidden001() {
        activeAdmin.setStatus(UserStatus.DISABLED);
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(activeAdmin));

        assertThatThrownBy(() -> authDomainService.login("admin", "admin123"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode").isEqualTo("FORBIDDEN_001");
    }

    @Test
    void login_wrongPassword_throwsAuth001() {
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(activeAdmin));
        when(passwordService.matches("wrongpass", "$2a$10$hashedPassword")).thenReturn(false);

        assertThatThrownBy(() -> authDomainService.login("admin", "wrongpass"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode").isEqualTo("AUTH_001");
    }

    @Test
    void login_antiEnumeration_sameErrorForNotFoundAndWrongPassword() {
        // 用户不存在和密码错误都应返回 AUTH_001，防枚举攻击
        when(userRepository.findByUsername("nobody")).thenReturn(Optional.empty());
        BusinessException notFound = catchThrowableOfType(
                () -> authDomainService.login("nobody", "pass"), BusinessException.class);

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(activeAdmin));
        when(passwordService.matches("wrong", "$2a$10$hashedPassword")).thenReturn(false);
        BusinessException wrongPass = catchThrowableOfType(
                () -> authDomainService.login("admin", "wrong"), BusinessException.class);

        assertThat(notFound.getErrorCode()).isEqualTo(wrongPass.getErrorCode());
    }

    // ==================== validateToken ====================

    @Test
    void validateToken_delegatesToJwtService() {
        Map<String, Object> claims = Map.of("userId", 1L, "role", "ADMIN");
        when(jwtService.validateToken("some-token")).thenReturn(claims);

        Map<String, Object> result = authDomainService.validateToken("some-token");

        assertThat(result).isEqualTo(claims);
    }

    @Test
    void validateToken_invalidToken_returnsNull() {
        when(jwtService.validateToken("bad-token")).thenReturn(null);

        assertThat(authDomainService.validateToken("bad-token")).isNull();
    }
}
