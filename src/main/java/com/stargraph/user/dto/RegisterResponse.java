package com.stargraph.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 用户注册响应。
 */
@Data
@AllArgsConstructor
@Schema(name = "RegisterResponse", description = "用户注册成功后的基础信息。")
public class RegisterResponse {

    /** 用户 ID */
    @Schema(description = "用户 ID。", example = "1")
    private Long id;

    /** 页面展示名称 */
    @Schema(description = "页面展示名称。", example = "星图用户晨光000001")
    private String name;

    /** 用户头像地址 */
    @Schema(description = "用户头像地址。")
    private String avatar;
}
