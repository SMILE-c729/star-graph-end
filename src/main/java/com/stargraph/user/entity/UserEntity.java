package com.stargraph.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户信息实体，映射 sg_user 表。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("sg_user")
public class UserEntity {

    /** 用户主键，自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 手机号，注册和登录使用 */
    private String mobile;

    /** BCrypt 加密后的密码 */
    private String password;

    /** 账号状态：0 正常，1 超时锁定，2 锁定，9 失效 */
    private Integer status;

    /** 用户名，支持登录和展示兜底 */
    private String username;

    /** 邮箱地址 */
    private String email;

    /** 会员等级 */
    private Integer vipLevel;

    /** 逻辑删除标记：0 未删除，1 已删除 */
    @TableLogic
    private Integer deleted;

    /** 昵称，优先用于前端展示 */
    private String nickname;

    /** 头像地址 */
    private String avatar;

    /** 性别：0 未知 */
    private Integer gender;
}
