package com.awsome.shop.auth.application.api.dto.auth;

import lombok.Data;

/**
 * 令牌数据传输对象
 */
@Data
public class TokenDTO {

    private String token;
    private long expiresIn;

    public static TokenDTO of(String token, long expiresIn) {
        TokenDTO dto = new TokenDTO();
        dto.setToken(token);
        dto.setExpiresIn(expiresIn);
        return dto;
    }
}
