package com.awsome.shop.auth.infrastructure.security.api.service;

/**
 * 密码服务接口（bcrypt 加密/验证）
 */
public interface PasswordService {

    /**
     * 加密密码
     *
     * @param rawPassword 明文密码
     * @return bcrypt 加密后的密码
     */
    String encode(String rawPassword);

    /**
     * 验证密码
     *
     * @param rawPassword     明文密码
     * @param encodedPassword 加密后的密码
     * @return 匹配返回 true
     */
    boolean matches(String rawPassword, String encodedPassword);
}
