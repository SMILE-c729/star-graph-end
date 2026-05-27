package com.stargraph.user.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户登录请求。
 * 支持用户名或手机号二选一登录，密码为必填项。
 */
@Data
public class LoginRequest {

    /** 用户名登录字段 */
    private String username;

    /** 手机号登录字段 */
    private String mobile;

    /** 登录密码 */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 自定义组合校验：用户名和手机号至少填写一个。
     */
    @AssertTrue(message = "手机号或用户名不能为空")
    public boolean isAccountPresent() {
        return hasText(username) || hasText(mobile);
    }

    /**
     * 获取实际登录账号，优先使用用户名。
     */
    public String getLoginAccount() {
        if (hasText(username)) {
            return username.trim();
        }
        return mobile.trim();
    }

    /**
     * 轻量字符串判空，避免为 DTO 单独引入工具依赖。
     */
    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
