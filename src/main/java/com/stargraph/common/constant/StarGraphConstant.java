package com.stargraph.common.constant;

import java.time.Duration;

/**
 * 项目通用常量入口。
 * 只收敛跨层复用、具备明确业务含义的常量；接口路径、请求参数等局部字符串保留在使用处。
 */
public interface StarGraphConstant {

    interface ResponseCode {
        int SUCCESS = 200;
        int BAD_REQUEST = 400;
        int UNAUTHORIZED = 401;
        int INTERNAL_SERVER_ERROR = 500;
        int BAD_GATEWAY = 502;
    }

    interface ErrorMessage {
        interface Common {
            String VALIDATION_FAILED = "参数校验失败";
            String COMFYUI_UNAVAILABLE_PREFIX = "ComfyUI 服务不可用: ";
            String INTERNAL_ERROR_PREFIX = "系统内部错误: ";
        }

        interface User {
            String MOBILE_REGISTERED = "该手机号已注册，请直接登录";
            String USERNAME_EXISTS = "用户名已存在，请更换用户名";
            String USER_NOT_FOUND = "用户不存在";
            String PASSWORD_INCORRECT = "密码不正确";
            String CODE_EXPIRED = "验证码已过期，请重新获取";
            String CODE_INCORRECT = "验证码不正确";
            String ACCOUNT_STATUS_ERROR = "账号状态异常";
            String ACCOUNT_TIMEOUT_LOCKED = "账号超时锁定，请稍后再试";
            String ACCOUNT_LOCKED = "账号已锁定，请联系管理员";
            String ACCOUNT_DISABLED = "账号已失效";
        }

        interface Token {
            String BLANK = "令牌不能为空";
            String INVALID = "令牌无效";
            String EXPIRED = "令牌已过期，请重新登录";
            String MISSING_EXPIRE_TIME = "令牌缺少过期时间";
            String MISSING_USER_ID = "令牌缺少用户ID";
            String MISSING_USERNAME = "令牌缺少用户名";
        }

        interface ComfyUi {
            String GET_HISTORY = "获取 ComfyUI 历史记录失败";
            String GET_HISTORY_DETAIL = "获取 ComfyUI 历史详情失败";
            String PREVIEW_IMAGE = "预览 ComfyUI 图片失败";
            String GET_SYSTEM_STATS = "获取 ComfyUI 系统信息失败";
            String GET_OBJECT_INFO = "获取 ComfyUI 节点信息失败";
            String INTERRUPT = "取消 ComfyUI 任务失败";
            String GET_QUEUE = "获取 ComfyUI 队列失败";
            String DELETE_QUEUE = "删除 ComfyUI 队列任务失败";
            String GET_PROMPT = "获取 ComfyUI 提示词信息失败";
            String SUBMIT_PROMPT = "提交 ComfyUI 工作流失败";
            String UPLOAD_IMAGE = "上传 ComfyUI 图片失败";
            String UPLOAD_MASK = "上传 ComfyUI 蒙版失败";
        }
    }

    interface UserStatus {
        int NORMAL = 0;
        int TIMEOUT_LOCKED = 1;
        int LOCKED = 2;
        int DISABLED = 9;
    }

    interface LogicDelete {
        int NOT_DELETED = 0;
        int DELETED = 1;
    }

    interface UserDefault {
        int VIP_LEVEL = 0;
        int UNKNOWN_GENDER = 0;
        String AVATAR = "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif?imageView2/1/w/80/h/80";
    }

    interface VerificationCode {
        int TTL_SECONDS = 60;
        int RANDOM_BOUND = 1_000_000;
        String SIX_DIGIT_FORMAT = "%06d";
        String REDIS_KEY_PREFIX = "stargraph:register:code:";
    }

    interface JwtConfig {
        String SECRET = "star-graph-user-token-secret";
        long TOKEN_TTL_HOURS = 72;
        Duration TOKEN_TTL = Duration.ofHours(TOKEN_TTL_HOURS);
    }

    interface JwtClaim {
        String ID = "id";
        String USERNAME = "username";
        String ISSUED_AT = "issuedAt";
        String EXPIRE_TIME = "expireTime";
        String IAT = "iat";
        String EXP = "exp";
    }
}
