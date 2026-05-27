package com.stargraph.user.service;

import com.stargraph.user.dto.LoginRequest;
import com.stargraph.user.dto.LoginResponse;
import com.stargraph.user.dto.RegisterRequest;
import com.stargraph.user.dto.SendCodeResponse;

/**
 * 用户账号业务服务接口。
 * 控制器依赖接口而非具体实现，便于后续替换验证码、密码策略或登录方式。
 */
public interface UserService {

    /**
     * 发送注册验证码。
     *
     * @param mobile 手机号
     * @return 验证码发送结果
     */
    SendCodeResponse sendCode(String mobile);

    /**
     * 注册新用户。
     *
     * @param request 注册请求
     */
    void register(RegisterRequest request);

    /**
     * 用户登录。
     *
     * @param request 登录请求
     * @return 登录后的用户摘要和令牌
     */
    LoginResponse login(LoginRequest request);
}
