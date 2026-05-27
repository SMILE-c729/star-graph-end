package com.stargraph.user.controller;

import com.stargraph.common.response.Result;
import com.stargraph.user.dto.LoginRequest;
import com.stargraph.user.dto.LoginResponse;
import com.stargraph.user.dto.RegisterRequest;
import com.stargraph.user.dto.SendCodeRequest;
import com.stargraph.user.dto.SendCodeResponse;
import com.stargraph.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户账号控制器。
 * 负责注册、登录和验证码相关 HTTP 入口，业务规则统一委托给 UserService。
 */
@RestController
@RequestMapping("/api/1.0/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /** 发送注册验证码。 */
    @PostMapping("/code")
    public Result<SendCodeResponse> sendCode(@Valid @RequestBody SendCodeRequest request) {
        return Result.ok("验证码生成成功", userService.sendCode(request.getMobile()));
    }

    /** 注册新用户。 */
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        userService.register(request);
        return Result.ok("注册成功", null);
    }

    /** 用户登录并返回 JWT。 */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.ok(userService.login(request));
    }
}
