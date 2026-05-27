package com.stargraph.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 注册验证码发送响应。
 * 返回验证码和过期时间，便于前端在开发阶段展示或倒计时。
 */
@Data
@AllArgsConstructor
public class SendCodeResponse {

    /** 生成的六位验证码 */
    private String code;

    /** 验证码有效期，单位秒 */
    private Integer ttl;
}
