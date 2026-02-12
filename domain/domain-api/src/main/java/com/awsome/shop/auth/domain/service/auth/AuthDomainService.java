package com.awsome.shop.auth.domain.service.auth;

import com.awsome.shop.auth.domain.model.user.UserEntity;

/**
 * 认证领域服务接口
 */
public interface AuthDomainService {

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录成功的用户实体
     */
    UserEntity login(String username, String password);

    /**
     * 用户登出
     *
     * @param token JWT Token
     */
    void logout(String token);
}
