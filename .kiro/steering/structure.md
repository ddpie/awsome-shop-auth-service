# 项目结构

DDD + 六边形架构，以 Maven 多模块项目组织。每一层拆分为 API（接口）和 impl（实现）子模块，以强制依赖倒置。

## 模块布局

```
├── common/                              # 公共工具（异常、错误码、Result、PageResult）
├── domain/
│   ├── domain-model/                    # 领域实体：UserEntity、Role、UserStatus
│   ├── domain-api/                      # 领域服务接口：AuthDomainService、UserDomainService
│   ├── domain-impl/                     # 领域服务实现（@Service）
│   ├── repository-api/                  # 仓储端口：UserRepository
│   ├── cache-api/                       # 缓存端口接口（预留）
│   ├── mq-api/                          # 消息队列端口接口（预留）
│   └── security-api/                    # 安全端口：JwtService、PasswordService
├── infrastructure/
│   ├── repository/
│   │   └── mysql-impl/                  # MySQL 适配器：UserPO、UserMapper、UserRepositoryImpl
│   ├── cache/
│   │   └── redis-impl/                  # Redis 适配器（预留）
│   ├── mq/
│   │   └── sqs-impl/                    # SQS 适配器（预留）
│   └── security/
│       └── jwt-impl/                    # JWT 适配器：JwtServiceImpl、BcryptPasswordServiceImpl
├── application/
│   ├── application-api/                 # 应用服务接口 + DTO + Request 对象
│   └── application-impl/               # 应用服务实现：AuthApplicationServiceImpl、UserApplicationServiceImpl
├── interface/
│   ├── interface-http/                  # REST 控制器：AuthController、UserController、GlobalExceptionHandler
│   └── interface-consumer/              # SQS 消息消费者（预留）
└── bootstrap/                           # Spring Boot 启动入口、配置、Flyway 迁移脚本
```

## 依赖规则（严格执行）
- `interface` → 仅依赖 `application-api`（禁止依赖 application-impl，禁止直接依赖 common）
- `application-impl` → 仅依赖 `domain-api`（禁止依赖 repository 或 infrastructure）
- `domain-impl` → 依赖端口接口（`repository-api`、`cache-api`、`mq-api`、`security-api`）
- `infrastructure/*-impl` → 实现端口接口
- `bootstrap` 聚合所有 impl 模块，用于 Spring DI 装配
- **禁止修改 pom.xml**：任何模块的 pom.xml 文件未经确认不得修改

## 包命名约定
基础包名：`com.awsome.shop.auth`

| 层级 | 包命名规则 | 现有实现 |
|---|---|---|
| 领域模型 | `domain.model.{aggregate}` | `domain.model.user`（UserEntity、Role、UserStatus） |
| 领域服务接口 | `domain.service.{aggregate}` | `domain.service.auth`、`domain.service.user` |
| 领域服务实现 | `domain.impl.service.{aggregate}` | `domain.impl.service.auth`、`domain.impl.service.user` |
| 仓储端口 | `repository.{aggregate}` | `repository.user`（UserRepository） |
| 仓储 MySQL 实现 | `repository.mysql.impl.{aggregate}` | `repository.mysql.impl.user` |
| 持久化对象 | `repository.mysql.po.{aggregate}` | `repository.mysql.po.user`（UserPO） |
| MyBatis Mapper | `repository.mysql.mapper.{aggregate}` | `repository.mysql.mapper.user`（UserMapper） |
| 安全端口 | `infrastructure.security.{name}` | `infrastructure.security.crypto`（JwtService、PasswordService） |
| 安全实现 | `infrastructure.security.crypto` | JwtServiceImpl、BcryptPasswordServiceImpl |
| 应用服务接口 | `application.api.service.{aggregate}` | `application.api.service.auth`、`application.api.service.user` |
| 应用层 DTO | `application.api.dto.{aggregate}` | `application.api.dto.user`（UserDTO） |
| 应用层请求 DTO | `application.api.dto.{aggregate}.request` | `application.api.dto.auth.request`、`application.api.dto.user.request` |
| 应用服务实现 | `application.impl.service.{aggregate}` | `application.impl.service.auth`、`application.impl.service.user` |
| HTTP 控制器 | `facade.http.controller` | AuthController、UserController |
| HTTP 异常处理器 | `facade.http.exception` | GlobalExceptionHandler |

## Result 类说明
- 统一使用 `com.awsome.shop.auth.common.result.Result`（code 为 String 类型："SUCCESS"）
- 成功：`Result.success(data)` / `Result.success()`
- 失败：`Result.failure(errorCode, message)`
- `facade.http.response` 包下不再有 Result 类

## 各层关键模式

### 领域模型（`domain-model`）
- 纯 Java POJO，使用 `@Data`（Lombok），不使用 Spring 注解
- UserEntity 使用 `@EqualsAndHashCode(of = "id")` 基于 ID 判等
- Role 枚举：ADMIN、EMPLOYEE
- UserStatus 枚举：ACTIVE、DISABLED

### 领域服务（`domain-api` / `domain-impl`）
- 接口定义在 `domain-api`，实现在 `domain-impl` 中标注 `@Service`
- 通过 `@RequiredArgsConstructor` 进行构造器注入
- 仅依赖端口接口（repository-api、security-api 等）
- 领域规则违反时抛出 `BusinessException`，使用 `AuthErrorCode` 枚举
- 写操作使用 `@Transactional`

### 异常处理
- 异常层级：`BaseException` → `BusinessException` / `ParameterException` / `SystemException`
- `AuthErrorCode` 枚举实现 `ErrorCode` 接口
- 错误码格式：`{CATEGORY}_{SEQ}`，通过 `lastIndexOf("_")` 提取前缀映射 HTTP 状态码
- `GlobalExceptionHandler` 使用 `Result.failure()` 返回错误

### 数据库迁移
- Flyway 脚本位于 `bootstrap/src/main/resources/db/migration/`
- V2 包含 users 表创建 + admin 种子数据（admin/admin123）
- 表使用 `utf8mb4` 字符集、`InnoDB` 引擎
