package com.awsome.shop.auth.infrastructure.security.api.service;

import com.awsome.shop.auth.domain.model.user.Role;

import java.util.Map;

/**
 * JWT 令牌服务接口
 */
public interface JwtService {

    /**
     * 生成 JWT 令牌
     *
     * @param userId   用户ID
     * @param username 用户名
     * @param role     角色
     * @return JWT 令牌字符串
     */
    String generateToken(Long userId, String username, Role role);

    /**
     * 验证并解析 JWT 令牌
     *
     * @param token JWT 令牌
     * @return 包含 userId, username, role 的 Map；验证失败返回 null
     */
    Map<String, Object> validateToken(String token);

    /**
     * 获取令牌过期时间（秒）
     */
    long getExpirationSeconds();
}
