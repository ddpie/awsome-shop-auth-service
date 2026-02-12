package com.awsome.shop.auth.application.api.service.auth;

import com.awsome.shop.auth.application.api.dto.auth.LoginRequest;
import com.awsome.shop.auth.application.api.dto.auth.LoginResponse;

/**
 * 认证应用服务接口
 */
public interface AuthApplicationService {

    LoginResponse login(LoginRequest request);

    void logout(String token);
}
