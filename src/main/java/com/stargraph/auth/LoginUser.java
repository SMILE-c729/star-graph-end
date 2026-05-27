package com.stargraph.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 当前登录用户快照。
 * 存放从 JWT 中解析出的最小用户信息，避免在请求上下文里保存完整用户实体。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser {

    /** 用户主键 ID */
    private Long id;

    /** 用户名 */
    private String username;
}
