# Star Graph End

Spring Boot 后端项目，通过 Retrofit2 连接本地 ComfyUI 服务器，提供统一的 REST API 代理。

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| JDK | 17 | Java 运行环境 |
| Spring Boot | 3.2.5 | Web 框架 |
| MyBatis-Plus | 3.5.5 | ORM 框架 |
| Redis | - | 缓存（数据库 1） |
| Retrofit2 | 2.9.0 | HTTP 客户端（连接 ComfyUI） |
| OkHttp | 4.12.0 | HTTP 引擎 |
| MySQL | - | 数据持久化 |
| Lombok | 1.18.32 | 代码简化 |

## 环境配置

### MySQL
- 地址：`localhost:3306`
- 用户名：`root`
- 密码：`123456`
- 数据库：`stargraph`

### Redis
- 地址：`localhost:6379`
- 数据库：`1`
- 密码：`123456`

### ComfyUI
- 地址：`http://localhost:8000`

## 快速启动

```bash
# 1. 确保 MySQL 和 Redis 已启动
# 2. 创建数据库
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS stargraph DEFAULT CHARACTER SET utf8mb4;"

# 3. 编译项目
mvn clean compile

# 4. 启动项目
mvn spring-boot:run
```

服务启动后运行在 `http://localhost:8080`。

## API 接口

所有 ComfyUI 接口通过 `/api/comfyui` 前缀代理。

### 历史记录

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/comfyui/history?maxItems=2` | 获取历史记录 |
| GET | `/api/comfyui/history/{promptId}` | 获取某条历史记录 |

### 图片

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/comfyui/view?filename=aa.png&type=output&subfolder=` | 预览图片 |
| POST | `/api/comfyui/upload/image` | 上传图片（multipart） |
| POST | `/api/comfyui/upload/mask` | 上传蒙版（multipart） |

### 系统

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/comfyui/system-stats` | 获取系统信息 |
| GET | `/api/comfyui/object-info/{nodeName}` | 获取节点配置信息 |

### 任务队列

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/comfyui/queue` | 获取队列列表 |
| POST | `/api/comfyui/queue/delete` | 删除队列任务（body: `["id1","id2"]`） |
| POST | `/api/comfyui/interrupt` | 取消当前执行 |

### 提示词

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/comfyui/prompt` | 获取提示词信息 |
| POST | `/api/comfyui/prompt` | 提交任务（body: `{"client_id":"xxx","prompt":{...}}`） |

## 统一响应格式

```json
{
    "code": 200,
    "message": "success",
    "data": { ... }
}
```

## 项目结构

```
src/main/java/com/stargraph/
├── StarGraphApplication.java          # 启动类
├── common/
│   ├── Result.java                    # 统一响应封装
│   └── GlobalExceptionHandler.java    # 全局异常处理
├── config/
│   ├── MyBatisPlusConfig.java         # MyBatis-Plus 配置
│   ├── RedisConfig.java               # Redis 配置
│   └── RetrofitConfig.java            # Retrofit 配置
├── comfyui/
│   ├── client/ComfyUiApi.java         # Retrofit 接口定义（12个方法）
│   ├── config/ComfyUiProperties.java  # ComfyUI 配置属性
│   ├── model/                         # 请求/响应模型
│   └── service/ComfyUiService.java    # ComfyUI 服务封装
├── controller/
│   └── ComfyUiController.java         # REST 控制器
├── entity/TaskEntity.java             # 任务实体
├── mapper/TaskMapper.java             # MyBatis-Plus Mapper
└── dto/SubmitTaskRequest.java         # 业务 DTO
```

## 开发说明

- ComfyUI 响应类型使用 `Map<String, Object>` 灵活接收，后续可替换为强类型模型
- Retrofit 采用同步调用，由 Spring Boot 内嵌 Tomcat 线程池处理并发
- 图片上传通过 Spring `MultipartFile` 转换为 OkHttp `MultipartBody.Part` 转发
