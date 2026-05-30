package com.stargraph.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户资料修改请求。
 */
@Data
@Schema(name = "UpdateProfileRequest", description = "用户资料修改请求体。")
public class UpdateProfileRequest {

    /** 登录用户名 */
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5A-Za-z0-9_]{2,20}$", message = "用户名仅支持2-20位中文、字母、数字或下划线")
    private String username;

    /** 邮箱地址 */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Size(max = 50, message = "邮箱长度不能超过50位")
    private String email;

    /** 页面展示昵称 */
    @NotBlank(message = "昵称不能为空")
    @Size(max = 64, message = "昵称长度不能超过64位")
    private String nickname;

    /** 头像地址 */
    @Size(max = 500, message = "头像地址长度不能超过500位")
    private String avatar;

    /** 性别：0 未知，1 男，2 女 */
    private Integer gender;
}
