package com.stargraph.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 当前用户资料响应。
 */
@Data
@AllArgsConstructor
@Schema(name = "UserProfileResponse", description = "当前登录用户资料。")
public class UserProfileResponse {

    private Long id;

    /** 页面展示名称，等同 nickname */
    private String name;

    /** 登录用户名 */
    private String username;

    private String mobile;

    private String email;

    /** 用户昵称/展示名 */
    private String nickname;

    private String avatar;

    private Integer gender;

    private Integer vipLevel;
}
