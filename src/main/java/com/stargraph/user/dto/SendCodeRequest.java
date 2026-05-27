package com.stargraph.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 注册验证码发送请求。
 * 当前仅支持手机号验证码。
 */
@Data
public class SendCodeRequest {

    /** 接收验证码的手机号 */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String mobile;
}
