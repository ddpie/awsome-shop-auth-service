package com.awsome.shop.auth.application.api.service.auth;

import com.awsome.shop.auth.application.api.dto.auth.TokenDTO;
import com.awsome.shop.auth.application.api.dto.auth.ValidateTokenDTO;
import com.awsome.shop.auth.application.api.dto.auth.request.LoginRequest;
import com.awsome.shop.auth.application.api.dto.auth.request.RegisterRequest;
import com.awsome.shop.auth.application.api.dto.auth.request.ValidateTokenRequest;
import com.awsome.shop.auth.application.api.dto.user.UserDTO;

/**
 * 认证应用服务接口
 */
public interface AuthApplicationService {

    UserDTO register(RegisterRequest request);

    TokenDTO login(LoginRequest request);

    ValidateTokenDTO validateToken(ValidateTokenRequest request);
}
