package com.stargraph.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户注册请求。
 * 包含手机号、验证码、邮箱、用户名和密码，并在 DTO 层完成格式校验。
 */
@Data
@Schema(name = "RegisterRequest", description = "用户注册请求体，包含手机号、验证码、邮箱、用户名和密码。")
public class RegisterRequest {

    /** 注册手机号 */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "注册手机号；必须是中国大陆 11 位手机号，且不能已注册。", example = "13800138000", requiredMode = Schema.RequiredMode.REQUIRED)
    private String mobile;

    /** 注册验证码 */
    @NotBlank(message = "验证码不能为空")
    @Pattern(regexp = "^\\d{6}$", message = "验证码必须是6位数字")
    @Schema(description = "注册验证码；调用发送验证码接口后获得，必须是 6 位数字。", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    /** 邮箱地址 */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Size(max = 50, message = "邮箱长度不能超过50位")
    @Schema(description = "邮箱地址；最长 50 个字符，需符合邮箱格式。", example = "star_user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    /** 用户名，允许中文、字母、数字和下划线 */
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5A-Za-z0-9_]{2,20}$", message = "用户名仅支持2-20位中文、字母、数字或下划线")
    @Schema(description = "用户名；2-20 位，仅支持中文、字母、数字或下划线。", example = "star_user", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    /** 登录密码 */
    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^\\S{8,20}$", message = "密码必须是8-20位非空白字符")
    @Schema(description = "登录密码；8-20 位非空白字符。", example = "StarGraph@123", format = "password", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
