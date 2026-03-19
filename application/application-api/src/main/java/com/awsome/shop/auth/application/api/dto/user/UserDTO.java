package com.awsome.shop.auth.application.api.dto.user;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户数据传输对象
 */
@Data
public class UserDTO {

    private Long id;
    private String username;
    private String name;
    private String employeeId;
    private String role;
    private String status;
    private Integer pointBalance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
