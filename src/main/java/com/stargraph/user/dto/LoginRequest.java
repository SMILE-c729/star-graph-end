package com.stargraph.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户登录请求。
 * 支持用户名或手机号二选一登录，密码为必填项。
 */
@Data
@Schema(name = "LoginRequest", description = "用户登录请求；username 与 mobile 二选一填写，password 必填。")
public class LoginRequest {

    /** 用户名登录字段 */
    @Schema(description = "用户名登录字段；和 mobile 二选一，若同时传入 username 与 mobile，后端优先使用 username。", example = "star_user")
    private String username;

    /** 手机号登录字段 */
    @Schema(description = "手机号登录字段；和 username 二选一，格式为中国大陆 11 位手机号。", example = "13800138000")
    private String mobile;

    /** 登录密码 */
    @NotBlank(message = "密码不能为空")
    @Schema(description = "登录密码，不能为空。", example = "StarGraph@123", format = "password", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    /**
     * 自定义组合校验：用户名和手机号至少填写一个。
     */
    @AssertTrue(message = "手机号或用户名不能为空")
    @JsonIgnore
    @Schema(hidden = true)
    public boolean isAccountPresent() {
        return hasText(username) || hasText(mobile);
    }

    /**
     * 获取实际登录账号，优先使用用户名。
     */
    @JsonIgnore
    @Schema(hidden = true)
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
