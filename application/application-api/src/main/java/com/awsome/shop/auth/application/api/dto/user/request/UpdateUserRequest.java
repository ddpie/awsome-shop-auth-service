package com.awsome.shop.auth.application.api.dto.user.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新用户请求（管理员操作）
 */
@Data
public class UpdateUserRequest {

    @NotNull(message = "用户ID不能为空")
    private Long id;

    private String name;

    private String status;

    private Long operatorId;
}
