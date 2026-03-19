package com.awsome.shop.auth.application.impl.service.auth;

import com.awsome.shop.auth.application.api.dto.auth.TokenDTO;
import com.awsome.shop.auth.application.api.dto.auth.ValidateTokenDTO;
import com.awsome.shop.auth.application.api.dto.auth.request.LoginRequest;
import com.awsome.shop.auth.application.api.dto.auth.request.RegisterRequest;
import com.awsome.shop.auth.application.api.dto.auth.request.ValidateTokenRequest;
import com.awsome.shop.auth.application.api.dto.user.UserDTO;
import com.awsome.shop.auth.application.api.client.PointsServiceClient;
import com.awsome.shop.auth.application.api.service.auth.AuthApplicationService;
import com.awsome.shop.auth.domain.model.user.UserEntity;
import com.awsome.shop.auth.domain.service.auth.AuthDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 认证应用服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthApplicationServiceImpl implements AuthApplicationService {

    private final AuthDomainService authDomainService;
    private final PointsServiceClient pointsServiceClient;

    @Override
    public UserDTO register(RegisterRequest request) {
        UserEntity user = authDomainService.register(
                request.getUsername(),
                request.getPassword(),
                request.getName(),
                request.getEmployeeId()
        );

        // 异步调用积分服务初始化（降级处理：失败不影响注册）
        try {
            initUserPoints(user.getId());
        } catch (Exception e) {
            log.warn("积分初始化失败，用户ID: {}，原因: {}", user.getId(), e.getMessage());
        }

        return toDTO(user);
    }

    @Override
    public TokenDTO login(LoginRequest request) {
        String token = authDomainService.login(request.getUsername(), request.getPassword());
        return TokenDTO.of(token, authDomainService.getTokenExpirationSeconds());
    }

    @Override
    public ValidateTokenDTO validateToken(ValidateTokenRequest request) {
        Map<String, Object> claims = authDomainService.validateToken(request.getToken());
        if (claims == null) {
            return ValidateTokenDTO.failure("Token无效或已过期");
        }
        Long userId = (Long) claims.get("userId");
        String role = (String) claims.get("role");
        if (userId == null || role == null) {
            log.warn("Token claims 不完整: userId={}, role={}", userId, role);
            return ValidateTokenDTO.failure("Token载荷不完整");
        }
        return ValidateTokenDTO.success(userId, role);
    }

    private static final int INITIAL_POINTS = 10000;

    private void initUserPoints(Long userId) {
        pointsServiceClient.initPoints(userId, INITIAL_POINTS);
    }

    private UserDTO toDTO(UserEntity entity) {
        UserDTO dto = new UserDTO();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setName(entity.getName());
        dto.setEmployeeId(entity.getEmployeeId());
        dto.setRole(entity.getRole().name());
        dto.setStatus(entity.getStatus().name());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
