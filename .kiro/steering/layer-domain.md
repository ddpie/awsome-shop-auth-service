---
inclusion: fileMatch
fileMatchPattern: "domain/**"
---

# Domain 层编码规则

领域层是业务核心，包含领域模型、领域服务接口/实现、以及各类 Port 接口。

## 子模块职责

### domain-model
- 纯 Java POJO，不依赖 Spring 或任何框架注解
- 实体命名：`{Name}Entity`，使用 `@Data` + `@EqualsAndHashCode(of = "id")`
- 业务行为方法直接定义在实体上（充血模型）
- 包路径：`domain.model.{aggregate}`
- 现有实现：
  - `UserEntity` — 用户实体（id、username、password、role、status、phone、email 等）
  - `Role` 枚举 — ADMIN、EMPLOYEE
  - `UserStatus` 枚举 — ACTIVE、DISABLED

### domain-api
- 领域服务接口，定义业务操作契约
- 命名：`{Name}DomainService`
- 包路径：`domain.service.{aggregate}`
- 方法参数使用基本类型或领域实体，不使用 DTO
- 现有实现：
  - `AuthDomainService` — register、login、validateToken
  - `UserDomainService` — getUserById、listUsersByPage、updateUser

### domain-impl
- 领域服务实现，使用 `@Service` + `@RequiredArgsConstructor`
- 命名：`{Name}DomainServiceImpl`
- 包路径：`domain.impl.service.{aggregate}`
- 只依赖 Port 接口，绝不直接依赖基础设施实现
- 业务校验失败时抛出 `BusinessException(AuthErrorCode.XXX)`
- 写操作添加 `@Transactional`
- 现有实现：
  - `AuthDomainServiceImpl` — 依赖 UserRepository、JwtService、PasswordService
  - `UserDomainServiceImpl` — 依赖 UserRepository
- 登录安全：错误密码与用户不存在返回相同错误码（防枚举攻击）

### Port 接口
- 定义基础设施访问契约，由 infrastructure 层实现
- Repository 接口返回领域实体，不暴露持久化细节
- 现有实现：
  - `repository-api` → `UserRepository`（findByUsername、findById、save、listByPage、update）
  - `security-api` → `JwtService`（generateToken、validateToken、getExpiration）、`PasswordService`（encode、matches）

## 禁止事项
- domain-model 中不允许出现 Spring 注解
- domain-api/domain-impl 不允许直接依赖 infrastructure 实现类
- 不允许在领域层处理 HTTP 请求/响应相关逻辑
- 不允许在领域层引用 DTO 或 Request 对象
