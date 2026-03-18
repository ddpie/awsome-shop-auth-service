# 认证服务概述

Awsome Shop Auth Service — 电商平台认证与用户管理微服务，基于领域驱动设计（DDD）和六边形架构构建。

## 核心职责
- 用户注册与登录（JWT 令牌签发）
- 令牌验证（供 Gateway 内部调用）
- 用户信息管理（查询、更新、禁用/启用）
- 分页查询用户列表（管理后台）

## API 端点

| 路径 | 作用域 | 说明 |
|---|---|---|
| `/api/v1/public/auth/register` | public | 用户注册 |
| `/api/v1/public/auth/login` | public | 用户登录，返回 JWT |
| `/api/v1/internal/auth/validate` | internal | 网关调用，验证 JWT 令牌 |
| `/api/v1/private/user/current` | private | 获取当前登录用户信息 |
| `/api/v1/admin/user/get` | admin | 管理员查询用户详情 |
| `/api/v1/admin/user/list` | admin | 管理员分页查询用户列表 |
| `/api/v1/admin/user/update` | admin | 管理员更新用户信息 |

## 作用域说明
- `public` — 无需认证，经 API Gateway 对外暴露
- `private` — 需认证，Gateway 注入 X-Operator-Id / X-User-Role 头
- `admin` — 需认证 + ADMIN 角色
- `internal` — 微服务间内部调用，不对外暴露

## 错误码（AuthErrorCode）
- `AUTH_001` — 用户名或密码错误（401）
- `AUTH_002` — 无效令牌（401）
- `CONFLICT_001` — 用户名已存在（409）
- `NOT_FOUND_001` — 用户不存在（404）
- `LOCKED_001` — 账户已被禁用（423）
- `PARAM_001` — 参数错误（400）

## 跨服务交互
- Gateway → Auth：POST `/api/v1/internal/auth/validate`，请求体 `{ "token": "..." }`
- Auth 响应：`{ "success": true, "operatorId": 1, "role": "ADMIN", "message": "..." }`
- Gateway 将 `operatorId` 和 `role` 注入下游请求头（X-Operator-Id、X-User-Role）
