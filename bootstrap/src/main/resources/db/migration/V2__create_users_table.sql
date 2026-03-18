CREATE TABLE `users` (
   `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
   `username`    VARCHAR(50)  NOT NULL COMMENT '用户名',
   `password`    VARCHAR(100) NOT NULL COMMENT '密码（bcrypt加密）',
   `name`        VARCHAR(50)  NOT NULL COMMENT '姓名',
   `employee_id` VARCHAR(50)  NOT NULL COMMENT '工号',
   `role`        VARCHAR(20)  NOT NULL DEFAULT 'EMPLOYEE' COMMENT '角色：EMPLOYEE/ADMIN',
   `status`      VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE/DISABLED',
   `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
   `created_by`  BIGINT                DEFAULT NULL COMMENT '创建人',
   `updated_by`  BIGINT                DEFAULT NULL COMMENT '更新人',
   `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
   `version`     INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
   PRIMARY KEY (`id`),
   UNIQUE INDEX `uk_username` (`username`, `deleted`),
   UNIQUE INDEX `uk_employee_id` (`employee_id`, `deleted`),
   INDEX `idx_status` (`status`),
   INDEX `idx_role` (`role`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表';

-- 初始化管理员账号（密码: admin123，bcrypt加密）
INSERT INTO `users` (`username`, `password`, `name`, `employee_id`, `role`, `status`)
VALUES ('admin', '$2a$10$HQUlyFzA/Gr9hYZVeKaHuucixkLkMwKiwd/6Rh.cJos22ANLPNlj.', '系统管理员', 'EMP000', 'ADMIN', 'ACTIVE');
