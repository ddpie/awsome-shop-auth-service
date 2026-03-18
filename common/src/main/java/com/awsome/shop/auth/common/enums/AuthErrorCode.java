package com.awsome.shop.auth.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 认证服务错误码
 */
@Getter
@AllArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    CONFLICT_001("CONFLICT_001", "用户名已存在"),
    CONFLICT_002("CONFLICT_002", "工号已存在"),
    AUTH_001("AUTH_001", "用户名或密码错误"),
    NOT_FOUND_001("NOT_FOUND_001", "用户不存在"),
    BAD_REQUEST_001("BAD_REQUEST_001", "请求参数校验失败"),
    FORBIDDEN_001("FORBIDDEN_001", "账号已被禁用"),
    BAD_REQUEST_002("BAD_REQUEST_002", "不能禁用自己的账号");

    private final String code;
    private final String message;
}
