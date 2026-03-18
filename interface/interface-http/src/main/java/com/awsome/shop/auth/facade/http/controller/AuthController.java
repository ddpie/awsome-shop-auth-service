package com.awsome.shop.auth.facade.http.controller;

import com.awsome.shop.auth.application.api.dto.auth.TokenDTO;
import com.awsome.shop.auth.application.api.dto.auth.ValidateTokenDTO;
import com.awsome.shop.auth.application.api.dto.auth.request.LoginRequest;
import com.awsome.shop.auth.application.api.dto.auth.request.RegisterRequest;
import com.awsome.shop.auth.application.api.dto.auth.request.ValidateTokenRequest;
import com.awsome.shop.auth.application.api.dto.user.UserDTO;
import com.awsome.shop.auth.application.api.service.auth.AuthApplicationService;
import com.awsome.shop.auth.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证 Controller
 */
@Slf4j
@Tag(name = "Auth", description = "认证相关接口")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {

    private final AuthApplicationService authApplicationService;

    @Operation(summary = "用户注册")
    @PostMapping("/public/auth/register")
    public Result<UserDTO> register(@RequestBody @Valid RegisterRequest request) {
        return Result.success(authApplicationService.register(request));
    }

    @Operation(summary = "用户登录")
    @PostMapping("/public/auth/login")
    public Result<TokenDTO> login(@RequestBody @Valid LoginRequest request) {
        return Result.success(authApplicationService.login(request));
    }

    @Operation(summary = "验证Token（内部接口）")
    @PostMapping("/internal/auth/validate")
    public ValidateTokenDTO validateToken(@RequestBody @Valid ValidateTokenRequest request) {
        try {
            return authApplicationService.validateToken(request);
        } catch (Exception e) {
            log.error("Token 验证发生异常", e);
            return ValidateTokenDTO.failure("内部验证错误");
        }
    }
}
