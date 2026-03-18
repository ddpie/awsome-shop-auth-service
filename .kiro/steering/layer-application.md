---
inclusion: fileMatch
fileMatchPattern: "application/**"
---

# Application 层编码规则

应用层编排领域服务，处理用例流程，是 interface 层与 domain 层之间的桥梁。

## 子模块

### application-api
- 应用服务接口 + DTO + Request 对象
- 服务接口命名：`{Name}ApplicationService`
- 包路径：`application.api.service.{aggregate}`
- 现有实现：
  - `AuthApplicationService` — register、login、validateToken
  - `UserApplicationService` — getCurrentUser、getUser、listUsers、updateUser

#### DTO
- 命名：`{Name}DTO`，使用 `@Data`
- 包路径：`application.api.dto.{aggregate}`
- 只包含需要对外暴露的字段，不暴露内部领域细节（如 password）
- 现有实现：`UserDTO`

#### Request 对象
- 包路径：`application.api.dto.{aggregate}.request`
- 使用 Jakarta Validation 注解校验：`@NotBlank`、`@NotNull`、`@Size`、`@Min`、`@Max` 等
- 校验消息使用中文
- 现有实现：
  - `RegisterRequest` / `LoginRequest`（auth 聚合）
  - `GetUserRequest` / `ListUsersRequest` / `UpdateUserRequest`（user 聚合）
- 注意：`UpdateUserRequest.operatorId` 为 Long 类型（由 Gateway 注入的 X-Operator-Id 头）

### application-impl
- 应用服务实现，使用 `@Service` + `@RequiredArgsConstructor`
- 命名：`{Name}ApplicationServiceImpl`
- 包路径：`application.impl.service.{aggregate}`
- 只依赖 Domain Service 接口（`domain-api`），绝不直接依赖 Repository 或 Infrastructure
- 在 impl 中手动编写 `toDTO()` 私有方法完成 Entity → DTO 转换
- 事务管理在此层使用 `@Transactional`（如需要）
- 现有实现：`AuthApplicationServiceImpl`、`UserApplicationServiceImpl`

## 禁止事项
- application-impl 不允许直接依赖 repository-api 或任何基础设施接口
- 不允许在 DTO/Request 中包含业务逻辑
- 不允许在应用层抛出领域异常以外的自定义异常
