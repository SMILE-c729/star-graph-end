package com.stargraph.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 修改密码请求。
 */
@Data
@Schema(name = "ChangePasswordRequest", description = "用户修改密码请求体。")
public class ChangePasswordRequest {

    /** 当前旧密码 */
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    /** 新密码 */
    @NotBlank(message = "新密码不能为空")
    @Pattern(regexp = "^\\S{8,20}$", message = "新密码必须是8-20位非空白字符")
    private String newPassword;
}
