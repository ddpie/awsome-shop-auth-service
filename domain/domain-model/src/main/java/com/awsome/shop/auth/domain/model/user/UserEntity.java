package com.awsome.shop.auth.domain.model.user;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户领域实体
 */
@Data
@EqualsAndHashCode(of = "id")
public class UserEntity {

    private Long id;
    private String username;
    private String password;
    private String name;
    private String employeeId;
    private Role role;
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 更新用户信息
     */
    public void updateInfo(String name, UserStatus status) {
        if (name != null) {
            this.name = name;
        }
        if (status != null) {
            this.status = status;
        }
    }

    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }
}
