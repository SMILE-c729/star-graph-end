package com.stargraph.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 注册验证码发送响应。
 * 返回验证码和过期时间，便于前端在开发阶段展示或倒计时。
 */
@Data
@AllArgsConstructor
@Schema(name = "SendCodeResponse", description = "注册验证码发送响应。")
public class SendCodeResponse {

    /** 生成的六位验证码 */
    @Schema(description = "生成的六位验证码；开发阶段返回给前端展示或联调使用。", example = "123456")
    private String code;

    /** 验证码有效期，单位秒 */
    @Schema(description = "验证码有效期，单位秒。", example = "300")
    private Integer ttl;
}
