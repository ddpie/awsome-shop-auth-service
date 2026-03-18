package com.awsome.shop.auth.application.api.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Token 验证请求（内部接口）
 */
@Data
public class ValidateTokenRequest {

    @NotBlank(message = "token不能为空")
    private String token;
}
