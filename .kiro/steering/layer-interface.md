---
inclusion: fileMatch
fileMatchPattern: "interface/**"
---

# Interface 层编码规则

接口层是系统的入口，负责接收外部请求并委托给应用层处理。

## 子模块

### interface-http（HTTP 控制器）
- 使用 `@RestController` + `@RequestMapping` + `@RequiredArgsConstructor`
- 命名：`{Name}Controller`
- 包路径：`facade.http.controller`
- 现有实现：
  - `AuthController` — /public/auth/register、/public/auth/login、/internal/auth/validate
  - `UserController` — /private/user/current、/admin/user/get、/admin/user/list、/admin/user/update

#### URL 设计规范
- 格式：`/api/v1/{scope}/{module}/{action}`，版本号之后必须是三段
  - **第一段 `{scope}`**：访问范围
    - `public` — 无需认证，经 API Gateway 对外暴露
    - `private` — 需认证，Gateway 注入用户信息头
    - `admin` — 需认证 + ADMIN 角色
    - `internal` — 微服务间内部调用
  - **第二段 `{module}`**：业务模块名（如 `auth`、`user`）
  - **第三段 `{action}`**：具体操作
- 所有端点均使用 `@PostMapping`（包括查询操作）
- Controller 类上 `@RequestMapping("/api/v1")`，方法上 `@PostMapping("/{scope}/{module}/{action}")`
- 请求体使用 `@RequestBody @Valid` 接收并校验
- 返回值统一使用 `Result<T>` 包装（`com.awsome.shop.auth.common.result.Result`）
- Swagger 注解：类上 `@Tag(name = "...", description = "...")`，方法上 `@Operation(summary = "...")`

#### 全局异常处理
- `GlobalExceptionHandler`（`@RestControllerAdvice`）统一捕获异常
- 使用 `Result.failure(errorCode, message)` 返回错误（String code）
- HTTP 状态码通过 `determineHttpStatus()` 方法从错误码前缀映射（使用 `lastIndexOf("_")`）
- `BusinessException` → 根据错误码前缀映射状态码
- `ParameterException` → 400
- `MethodArgumentNotValidException` → 400（含字段级错误详情）
- `SystemException` / `Exception` → 500

### interface-consumer（消息消费者）
- 处理 SQS 消息，委托给应用层服务（预留，暂未实现）

## 禁止事项
- Controller 只调用 Application Service 接口，不直接调用 Domain Service 或 Repository
- interface 层不直接依赖 common，通过 application-api 传递获得
- 不在 Controller 中编写业务逻辑
- 不在 Controller 中进行数据转换（转换由 Application 层负责）
- 不直接返回领域实体，必须通过 DTO
