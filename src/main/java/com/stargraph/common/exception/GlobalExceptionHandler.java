package com.stargraph.common.exception;

import com.stargraph.common.constant.StarGraphConstant;
import com.stargraph.common.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

/**
 * 全局异常处理器。
 * 统一捕获 Controller 层抛出的异常，转换为 Result 格式返回给前端。
 * 异常优先级：IOException（ComfyUI 连接）> MethodArgumentNotValidException（参数校验）> Exception（兜底）。
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** ComfyUI 连接异常（网络不通、服务挂了），返回 502 网关错误 */
    @ExceptionHandler(IOException.class)
    public Result<Void> handleIOException(IOException e) {
        log.error("ComfyUI 连接异常: {}", e.getMessage(), e);
        return Result.fail(StarGraphConstant.ResponseCode.BAD_GATEWAY,
                StarGraphConstant.ErrorMessage.Common.COMFYUI_UNAVAILABLE_PREFIX + e.getMessage());
    }

    /** @Valid 参数校验失败，收集所有字段错误信息拼接返回 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse(StarGraphConstant.ErrorMessage.Common.VALIDATION_FAILED);
        return Result.fail(StarGraphConstant.ResponseCode.BAD_REQUEST, message);
    }

    /** 业务参数异常，如手机号已注册、验证码错误、密码错误等 */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        return Result.fail(StarGraphConstant.ResponseCode.BAD_REQUEST, e.getMessage());
    }

    /** 兜底异常处理，防止未捕获异常导致返回 Spring 默认错误页 */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常: {}", e.getMessage(), e);
        return Result.fail(StarGraphConstant.ResponseCode.INTERNAL_SERVER_ERROR,
                StarGraphConstant.ErrorMessage.Common.INTERNAL_ERROR_PREFIX + e.getMessage());
    }
}
