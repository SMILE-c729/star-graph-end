package com.stargraph.common.response;

import com.stargraph.common.constant.StarGraphConstant;
import lombok.Data;

/**
 * 统一 API 响应封装类。
 * 所有 Controller 方法均返回 Result&lt;T&gt;，保证前端收到一致的 JSON 结构：{code, message, data}。
 * 使用泛型 T 使 data 字段类型安全，通过静态工厂方法 ok/fail 快速构造响应。
 */
@Data
public class Result<T> {

    /** 业务状态码：200 成功，400 参数错误，500 服务器错误，502 ComfyUI 不可用 */
    private int code;

    /** 提示信息，成功时为 "success"，失败时为具体错误描述 */
    private String message;

    /** 响应数据，失败时为 null */
    private T data;

    public Result() {}

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /** 成功响应，无数据 */
    public static <T> Result<T> ok() {
        return new Result<T>(StarGraphConstant.ResponseCode.SUCCESS, "success", null);
    }

    /** 成功响应，携带数据 */
    public static <T> Result<T> ok(T data) {
        return new Result<T>(StarGraphConstant.ResponseCode.SUCCESS, "success", data);
    }

    /** 成功响应，自定义消息和数据 */
    public static <T> Result<T> ok(String message, T data) {
        return new Result<T>(StarGraphConstant.ResponseCode.SUCCESS, message, data);
    }

    /** 失败响应，默认 500 状态码 */
    public static <T> Result<T> fail(String message) {
        return new Result<T>(StarGraphConstant.ResponseCode.INTERNAL_SERVER_ERROR, message, null);
    }

    /** 失败响应，自定义状态码和消息 */
    public static <T> Result<T> fail(int code, String message) {
        return new Result<T>(code, message, null);
    }
}
