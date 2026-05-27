# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Spring Boot 3.2.5 / Java 17 后端项目，作为本地 ComfyUI 服务器的 REST API 代理层。通过 Retrofit2/OkHttp 转发请求到 ComfyUI（默认 `localhost:8000`），并提供任务持久化能力（MySQL + MyBatis-Plus）。

## Build & Run Commands

```bash
# 编译
mvn clean compile

# 启动（需要 MySQL + Redis 已运行）
mvn spring-boot:run

# 运行单个测试
mvn test -Dtest=StarGraphApplicationTests

# 打包
mvn clean package -DskipTests
```

服务默认运行在 `http://localhost:8080`，激活 `dev` profile。

## Architecture

```
请求流向: Client → ComfyUiController → ComfyUiService → ComfyUiApi (Retrofit) → ComfyUI Server
```

### 分层结构（包 `com.stargraph`）

- **`common/`** — `Result<T>` 统一响应封装（code/message/data），`GlobalExceptionHandler` 全局异常处理
- **`config/`** — Spring 配置类：MyBatis-Plus 分页拦截器、RedisTemplate（Jackson 序列化）、OkHttp/Retrofit Bean
- **`comfyui/`** — ComfyUI 集成核心
  - `client/ComfyUiApi.java` — Retrofit 接口定义（12 个方法），返回类型多为 `Map<String, Object>`
  - `service/ComfyUiService.java` — 同步调用封装，内部 `executeCall`/`executeVoid` 处理异常
  - `properties/ComfyUiProperties.java` — `@ConfigurationProperties(prefix = "comfyui")` 绑定 baseUrl
  - `model/` — ComfyUI 请求/响应 DTO（PromptRequest、PromptResponse、QueueResponse 等）
- **`controller/`** — REST 控制器，`/api/comfyui/*` 前缀代理所有 ComfyUI 接口
- **`entity/` + `mapper/` + `dto/`** — 数据层：`TaskEntity`（comfyui_task 表）、`TaskMapper`、`SubmitTaskRequest`

### 关键设计决策

- Retrofit 采用**同步调用**，依赖 Spring Boot 内嵌 Tomcat 线程池处理并发
- ComfyUI 响应使用 `Map<String, Object>` 灵活接收，非强类型
- 图片上传通过 Spring `MultipartFile` → OkHttp `MultipartBody.Part` 转换转发
- MyBatis-Plus 启用逻辑删除（`deleted` 字段）、驼峰映射、自增主键
- `TaskEntity`/`TaskMapper` 已定义但 Controller 层尚未使用，属于预留数据层

## Infrastructure Dependencies

| 服务 | 地址 | 用途 |
|------|------|------|
| MySQL | `localhost:3306/stargraph` | 数据持久化 |
| Redis | `localhost:6379` db1 | 缓存（已配置，尚未在 Service 层使用） |
| ComfyUI | `localhost:8000` | AI 图像生成工作流引擎 |

配置文件：`src/main/resources/application.yaml`（主配置）、`application-dev.yml`（dev profile 覆盖）

## Conventions

- Lombok 用于所有实体/DTO 的 getter/setter/builder 等样板代码
- 统一响应格式：`Result.ok(data)` / `Result.fail(message)` — Controller 方法直接返回 `Result<T>`
- 提交消息格式：`<type>: <描述>`（type: feat, fix, refactor, docs, test, chore, perf, ci）
