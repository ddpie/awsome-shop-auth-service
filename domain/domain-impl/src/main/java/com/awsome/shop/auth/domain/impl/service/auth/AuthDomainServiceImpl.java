package com.awsome.shop.auth.domain.impl.service.auth;

import com.awsome.shop.auth.common.enums.AuthErrorCode;
import com.awsome.shop.auth.common.exception.BusinessException;
import com.awsome.shop.auth.domain.model.user.Role;
import com.awsome.shop.auth.domain.model.user.UserEntity;
import com.awsome.shop.auth.domain.model.user.UserStatus;
import com.awsome.shop.auth.domain.service.auth.AuthDomainService;
import com.awsome.shop.auth.infrastructure.security.api.service.JwtService;
import com.awsome.shop.auth.infrastructure.security.api.service.PasswordService;
import com.awsome.shop.auth.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

/**
 * 认证领域服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthDomainServiceImpl implements AuthDomainService {

    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final JwtService jwtService;

    @Override
    @Transactional
    public UserEntity register(String username, String password, String name, String employeeId) {
        // 用户名唯一性检查
        if (userRepository.existsByUsername(username)) {
            throw new BusinessException(AuthErrorCode.CONFLICT_001);
        }

        // 工号唯一性检查
        if (userRepository.existsByEmployeeId(employeeId)) {
            throw new BusinessException(AuthErrorCode.CONFLICT_002);
        }

        // 构建用户实体
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(passwordService.encode(password));
        user.setName(name);
        user.setEmployeeId(employeeId);
        user.setRole(Role.EMPLOYEE);
        user.setStatus(UserStatus.ACTIVE);

        return userRepository.save(user);
    }

    @Override
    public String login(String username, String password) {
        // 查找用户
        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new BusinessException(AuthErrorCode.AUTH_001);
        }

        UserEntity user = optionalUser.get();

        // 检查账号状态
        if (!user.isActive()) {
            throw new BusinessException(AuthErrorCode.FORBIDDEN_001);
        }

        // 验证密码
        if (!passwordService.matches(password, user.getPassword())) {
            throw new BusinessException(AuthErrorCode.AUTH_001);
        }

        // 生成 JWT
        return jwtService.generateToken(user.getId(), user.getUsername(), user.getRole());
    }

    @Override
    public Map<String, Object> validateToken(String token) {
        return jwtService.validateToken(token);
    }

    @Override
    public long getTokenExpirationSeconds() {
        return jwtService.getExpirationSeconds();
    }
}
