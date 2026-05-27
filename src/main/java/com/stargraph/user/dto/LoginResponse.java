package com.stargraph.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 用户登录响应。
 * 返回前端展示所需的基础用户信息和后续请求使用的 JWT。
 */
@Data
@AllArgsConstructor
@Schema(name = "LoginResponse", description = "用户登录响应，包含基础用户信息和后续接口调用所需的 JWT。")
public class LoginResponse {

    /** 用户 ID */
    @Schema(description = "用户 ID。", example = "1")
    private Long id;

    /** 展示名称，优先昵称，兜底用户名 */
    @Schema(description = "展示名称；优先使用昵称，昵称为空时使用用户名。", example = "star_user")
    private String name;

    /** 用户头像地址 */
    @Schema(description = "用户头像地址。", example = "https://example.com/avatar.png")
    private String avatar;

    /** 登录令牌 */
    @Schema(description = "JWT 登录令牌；调用需要登录的接口时在 Authorization 中携带 Bearer token。", example = "eyJhbGciOiJIUzI1NiJ9.example.signature")
    private String token;
}
