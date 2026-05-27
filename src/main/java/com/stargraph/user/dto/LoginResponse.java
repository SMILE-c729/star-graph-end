package com.stargraph.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 用户登录响应。
 * 返回前端展示所需的基础用户信息和后续请求使用的 JWT。
 */
@Data
@AllArgsConstructor
public class LoginResponse {

    /** 用户 ID */
    private Long id;

    /** 展示名称，优先昵称，兜底用户名 */
    private String name;

    /** 用户头像地址 */
    private String avatar;

    /** 登录令牌 */
    private String token;
}
