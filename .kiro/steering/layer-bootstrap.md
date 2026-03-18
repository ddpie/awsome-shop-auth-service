---
inclusion: fileMatch
fileMatchPattern: "bootstrap/**"
---

# Bootstrap 层编码规则

启动模块，负责 Spring Boot 应用组装、配置管理和数据库迁移。

## 职责
- Spring Boot 启动入口（`Application.java`）
- `@ComponentScan(basePackages = "com.awsome.shop.auth")` 扫描所有模块
- 聚合所有 impl 模块的 Maven 依赖，由 Spring DI 完成接口与实现的绑定
- 配置类（`config/` 包下）
- 多环境配置文件（`application-{profile}.yml`）
- Flyway 数据库迁移脚本

## 配置文件
- `application.yml` — 所有环境共享的基础配置
- `application-local.yml` — 本地开发（MySQL localhost:3306，端口 8001）
- `application-docker.yml` — Docker 部署（MySQL docker DNS，Redis docker DNS）
- 自定义配置项：`security.jwt.*`（secret、expiration、issuer）、`security.login.*`（max-failed-attempts、lock-duration）、`shop.security.encryption.key`

## 数据库迁移（Flyway）
- 脚本路径：`src/main/resources/db/migration/`
- 命名规范：`V{版本号}__{描述}.sql`（双下划线分隔）
- 现有脚本：
  - `V1__create_sample_table.sql` — 示例表
  - `V2__create_users_table.sql` — 用户表 + admin 种子数据（admin/admin123）
- 表规范：
  - 引擎：`InnoDB`，字符集：`utf8mb4`，排序：`utf8mb4_unicode_ci`
  - 必须字段：`id`（BIGINT AUTO_INCREMENT PK）、`created_at`、`updated_at`、`created_by`、`updated_by`、`deleted`（TINYINT DEFAULT 0）、`version`（INT DEFAULT 0）
  - 列名使用 `snake_case`，MyBatis-Plus 自动映射到 Java `camelCase`

## 禁止事项
- 不在 bootstrap 中编写业务逻辑
- 不在 bootstrap 中定义领域实体或 DTO
- 迁移脚本一旦提交不可修改，只能新增
