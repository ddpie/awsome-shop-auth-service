package com.awsome.shop.auth.domain.service.auth;

import com.awsome.shop.auth.domain.model.user.UserEntity;

import java.util.Map;

/**
 * 认证领域服务接口
 */
public interface AuthDomainService {

    /**
     * 用户注册
     *
     * @param username   用户名
     * @param password   明文密码
     * @param name       姓名
     * @param employeeId 工号
     * @return 注册成功的用户实体
     */
    UserEntity register(String username, String password, String name, String employeeId);

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 明文密码
     * @return JWT 令牌
     */
    String login(String username, String password);

    /**
     * 验证令牌
     *
     * @param token JWT 令牌
     * @return 包含 userId, username, role 的 Map；验证失败返回 null
     */
    Map<String, Object> validateToken(String token);

    /**
     * 获取令牌过期时间（秒）
     */
    long getTokenExpirationSeconds();
}
