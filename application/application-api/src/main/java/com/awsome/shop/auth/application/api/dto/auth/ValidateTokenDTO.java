package com.awsome.shop.auth.application.api.dto.auth;

import lombok.Data;

/**
 * Token 验证结果（内部接口响应）
 */
@Data
public class ValidateTokenDTO {

    private boolean success;
    private Long operatorId;
    private String role;
    private String message;

    public static ValidateTokenDTO success(Long operatorId, String role) {
        ValidateTokenDTO dto = new ValidateTokenDTO();
        dto.setSuccess(true);
        dto.setOperatorId(operatorId);
        dto.setRole(role);
        return dto;
    }

    public static ValidateTokenDTO failure(String message) {
        ValidateTokenDTO dto = new ValidateTokenDTO();
        dto.setSuccess(false);
        dto.setMessage(message);
        return dto;
    }
}
