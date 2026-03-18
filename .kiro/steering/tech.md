# 技术栈与构建

## 运行时与语言
- Java 21
- Spring Boot 3.4.1（Servlet/MVC 模式，非 WebFlux）
- Spring Cloud 2025.0.0

## 构建系统
- Maven（多模块 POM，26 个子模块）
- Lombok 1.18.36（注解处理器）
- JaCoCo 代码覆盖率

## ORM 与数据库
- MyBatis-Plus 3.5.7（Spring Boot 3 starter）
- MySQL 8.4，使用 Druid 连接池
- Flyway 管理数据库迁移（脚本位于 `bootstrap/src/main/resources/db/migration/`）
- 迁移脚本命名：`V{number}__{description}.sql`
- 现有脚本：`V1__create_sample_table.sql`、`V2__create_users_table.sql`（含 admin 种子数据）

## 缓存
- Spring Data Redis，使用 Lettuce 客户端

## 消息队列
- AWS SQS（SDK 2.20.0）

## 安全
- JJWT 0.12.3（JWT 创建/验证，HS256 算法）
- spring-security-crypto（BCrypt 密码加密）
- 认证在网关层统一处理，Auth 服务提供令牌签发与验证

## 测试框架
- JUnit 5 + Mockito（单元测试）
- 44 个单元测试覆盖 domain、application、infrastructure 三层

## 容器化
- 多阶段 Docker 构建：`maven:3.9-eclipse-temurin-21` → `eclipse-temurin:21-jre`
- 容器端口：8001
- 默认 profile：`docker`

## 常用命令

```bash
# 全量构建（跳过测试）
mvn clean install -DskipTests

# 运行测试
mvn test

# 启动应用（local 配置，端口 8001）
mvn spring-boot:run -pl bootstrap

# 使用指定配置启动
mvn spring-boot:run -pl bootstrap -Dspring-boot.run.profiles=local

# 构建单个模块
mvn clean install -pl domain/domain-model -am

# Docker 构建
docker build -t awsome-shop-auth-service .
```

## 环境配置
- `local`（默认）— 本地开发（localhost:3306）
- `docker` — Docker 部署（Docker DNS: mysql:3306）

## 环境变量（Docker 模式）
- `DB_HOST` / `DB_PORT` / `DB_NAME` / `DB_USERNAME` / `DB_PASSWORD` — 数据库连接
- `REDIS_HOST` / `REDIS_PORT` — Redis 连接
- `JWT_SECRET` / `JWT_EXPIRATION` — JWT 配置
- `ENCRYPTION_KEY` — 加密密钥
- `SPRING_PROFILES_ACTIVE` — 激活的配置文件
