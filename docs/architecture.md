# Auth Service 架构方案

## 1. 服务定位

认证服务（auth-service）负责 AWSomeShop 平台的用户管理与认证鉴权，是所有需认证请求的信任根。

| 属性 | 值 |
|------|-----|
| 端口 | 8001 |
| 框架 | Spring Boot 3.4.1 (Servlet/MVC) |
| 架构 | DDD + 六边形架构 |
| 数据库 | MySQL 8.4 (awsome_shop_auth) |

---

## 2. 核心职责

```
┌─────────────────────────────────────────────┐
│                 auth-service                │
├─────────────────────────────────────────────┤
│  用户注册（bcrypt 加密 + 积分初始化）        │
│  用户登录（密码验证 → JWT 签发）            │
│  Token 验证（供 gateway 调用的内部接口）     │
│  用户管理（查询/更新/禁用，管理员专属）       │
└─────────────────────────────────────────────┘
```

---

## 3. DDD 分层架构

```
Interface Layer          Application Layer         Domain Layer              Infrastructure Layer
┌──────────────┐  ──▶  ┌────────────────┐  ──▶  ┌──────────────────┐  ◀──  ┌──────────────────┐
│ AuthController│       │AuthAppService  │       │AuthDomainService │       │ JwtServiceImpl   │
│ UserController│       │UserAppService  │       │UserDomainService │       │ BcryptPassword   │
└──────────────┘       └────────────────┘       └──────────────────┘       │ UserRepoImpl     │
                                                  │  依赖 Port 接口    │       └──────────────────┘
                                                  ▼                         实现 Port 接口 ▲
                                                ┌──────────────────┐
                                                │  UserRepository  │ (Port)
                                                │  JwtService      │ (Port)
                                                │  PasswordService │ (Port)
                                                └──────────────────┘
```

### 依赖规则

| 规则 | 说明 |
|------|------|
| Interface → Application | Controller 只调用 Application Service 接口 |
| Application → Domain | 应用服务只调用 Domain Service 接口，不直接依赖 Repository |
| Domain → Port | 领域服务通过 Port 接口访问基础设施 |
| Infrastructure → Port | 基础设施模块实现 Port 接口（依赖反转） |

---

## 4. API 端点设计

所有端点均使用 **POST** 方法，URL 格式：`/api/v1/{scope}/{module}/{action}`

### 公开接口（无需认证）

| 端点 | 说明 | 请求体 | 响应 |
|------|------|--------|------|
| `/api/v1/public/auth/register` | 用户注册 | `{username, password, name, employeeId}` | `Result<UserDTO>` |
| `/api/v1/public/auth/login` | 用户登录 | `{username, password}` | `Result<TokenDTO>` |

### 已认证接口（需 Bearer Token）

| 端点 | 说明 | 请求体 | 响应 |
|------|------|--------|------|
| `/api/v1/private/user/current` | 获取当前用户 | 无（从 X-Operator-Id 头读取） | `Result<UserDTO>` |

### 管理员接口（需 ADMIN 角色）

| 端点 | 说明 | 请求体 | 响应 |
|------|------|--------|------|
| `/api/v1/admin/user/list` | 分页查询用户 | `{keyword?, page?, size?}` | `Result<PageResult<UserDTO>>` |
| `/api/v1/admin/user/get` | 查看用户详情 | `{id}` | `Result<UserDTO>` |
| `/api/v1/admin/user/update` | 更新用户状态 | `{id, name?, status?}` | `Result<UserDTO>` |

### 内部接口（仅 gateway 调用）

| 端点 | 说明 | 请求体 | 响应 |
|------|------|--------|------|
| `/api/v1/internal/auth/validate` | Token 验证 | `{token}` | `{success, operatorId, role, message}` |

> 注意：内部接口返回裸 DTO（非 Result 包装），直接匹配 gateway 的解析格式。

---

## 5. 安全设计

### 密码

| 项 | 方案 |
|----|------|
| 加密算法 | bcrypt (cost factor = 10) |
| 实现 | `spring-security-crypto` 的 `BCryptPasswordEncoder` |
| 存储 | 数据库只存储 bcrypt 哈希，明文永不持久化 |

### JWT

| 项 | 方案 |
|----|------|
| 签名算法 | HS256 (HMAC-SHA256) |
| 库 | JJWT 0.12.3 |
| Payload | `{userId(Long), username(String), role(String), iat, exp}` |
| 过期时间 | 配置项 `security.jwt.expiration`，默认 7200 秒 |
| 密钥 | 配置项 `security.jwt.secret`，至少 256 bits |

### 防枚举攻击

- 用户名不存在 和 密码错误 统一返回 AUTH_001（"用户名或密码错误"）
- 不向客户端暴露用户是否存在

---

## 6. 错误码体系

| 错误码 | HTTP 状态 | 含义 |
|--------|----------|------|
| CONFLICT_001 | 409 | 用户名已存在 |
| CONFLICT_002 | 409 | 工号已存在 |
| AUTH_001 | 401 | 用户名或密码错误 |
| NOT_FOUND_001 | 404 | 用户不存在 |
| BAD_REQUEST_001 | 400 | 请求参数校验失败 |
| BAD_REQUEST_002 | 400 | 不能禁用自己的账号 |
| FORBIDDEN_001 | 403 | 账号已被禁用 |

**映射规则**：错误码前缀 → HTTP 状态码，通过 `GlobalExceptionHandler` 使用 `lastIndexOf("_")` 提取前缀。

---

## 7. 数据模型

### users 表

```sql
CREATE TABLE users (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  username    VARCHAR(50)  NOT NULL,
  password    VARCHAR(100) NOT NULL,  -- bcrypt hash
  name        VARCHAR(50)  NOT NULL,
  employee_id VARCHAR(50)  NOT NULL,
  role        VARCHAR(20)  NOT NULL DEFAULT 'EMPLOYEE',   -- EMPLOYEE | ADMIN
  status      VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',     -- ACTIVE | DISABLED
  created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  created_by  BIGINT,
  updated_by  BIGINT,
  deleted     TINYINT NOT NULL DEFAULT 0,  -- 逻辑删除
  version     INT     NOT NULL DEFAULT 0,  -- 乐观锁
  UNIQUE INDEX uk_username(username, deleted),
  UNIQUE INDEX uk_employee_id(employee_id, deleted)
);
```

### 种子数据

| username | password | role | 说明 |
|----------|----------|------|------|
| admin | admin123 (bcrypt) | ADMIN | 系统管理员 |

---

## 8. 核心流程

### 注册流程

```
Client → Gateway → AuthController.register
  → AuthDomainService.register
    1. 检查用户名唯一 (UserRepository.existsByUsername)
    2. 检查工号唯一 (UserRepository.existsByEmployeeId)
    3. bcrypt 加密密码 (PasswordService.encode)
    4. 设置角色 EMPLOYEE，状态 ACTIVE
    5. 持久化 (UserRepository.save)
  → 降级调用积分服务初始化 (try-catch，失败不影响注册)
  → 返回 UserDTO
```

### 登录流程

```
Client → Gateway → AuthController.login
  → AuthDomainService.login
    1. 查找用户 (UserRepository.findByUsername)
    2. 检查账号状态 (isActive → FORBIDDEN_001)
    3. 验证密码 (PasswordService.matches → AUTH_001)
    4. 生成 JWT (JwtService.generateToken)
  → 返回 TokenDTO {token, expiresIn}
```

### Token 验证流程（内部接口）

```
Gateway → AuthController.validateToken
  → AuthDomainService.validateToken
    → JwtService.validateToken (签名 + 过期检查)
  → 检查 claims 完整性 (userId, role 非 null)
  → 返回 ValidateTokenDTO {success, operatorId, role}
```

---

## 9. 与 Gateway 的交互契约

```
Gateway ──POST──▶ auth-service:/api/v1/internal/auth/validate
         Body: {"token": "eyJhbG..."}

         ◀── 成功: {"success": true,  "operatorId": 1, "role": "ADMIN", "message": null}
         ◀── 失败: {"success": false, "operatorId": null, "role": null, "message": "Token无效或已过期"}
```

gateway 根据响应：
- `success=true` → 注入 `X-Operator-Id` 和 `X-User-Role` 头，转发请求
- `success=false` → 返回 401 AUTHZ_001

---

## 10. 跨服务依赖

| 依赖 | 类型 | 降级策略 |
|------|------|---------|
| MySQL (awsome_shop_auth) | 硬依赖 | 服务不可用 |
| points-service | 软依赖 | 注册时初始化积分失败不影响注册，记录 warn 日志 |
| JWT_SECRET | 配置依赖 | 与 gateway 共享，缺失则启动失败 |
