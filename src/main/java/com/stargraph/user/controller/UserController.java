package com.stargraph.user.controller;

import com.stargraph.common.response.Result;
import com.stargraph.user.dto.LoginRequest;
import com.stargraph.user.dto.LoginResponse;
import com.stargraph.user.dto.RegisterRequest;
import com.stargraph.user.dto.SendCodeRequest;
import com.stargraph.user.dto.SendCodeResponse;
import com.stargraph.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "用户账号接口", description = "注册验证码、用户注册、用户登录等账号相关接口。")
public class UserController {

    private final UserService userService;

    /** 发送注册验证码。 */
    @PostMapping("/code")
    @Operation(
            summary = "发送注册验证码",
            description = "根据手机号生成 6 位注册验证码，并写入 Redis。该接口用于注册前获取验证码。",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "验证码发送请求体，只需要传入接收验证码的手机号。",
                    content = @Content(
                            schema = @Schema(implementation = SendCodeRequest.class),
                            examples = @ExampleObject(
                                    name = "发送验证码示例",
                                    value = "{\"mobile\":\"13800138000\"}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "验证码生成成功", content = @Content(schema = @Schema(implementation = Result.class))),
                    @ApiResponse(responseCode = "400", description = "手机号格式错误或手机号已注册", content = @Content(schema = @Schema(implementation = Result.class)))
            }
    )
    public Result<SendCodeResponse> sendCode(@Valid @RequestBody SendCodeRequest request) {
        return Result.ok("验证码生成成功", userService.sendCode(request.getMobile()));
    }

    /** 注册新用户。 */
    @PostMapping("/register")
    @Operation(
            summary = "注册新用户",
            description = "使用手机号、验证码、邮箱、用户名和密码注册新用户。注册成功后验证码会从 Redis 中删除。",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "用户注册请求体，所有字段均必填。",
                    content = @Content(
                            schema = @Schema(implementation = RegisterRequest.class),
                            examples = @ExampleObject(
                                    name = "注册示例",
                                    value = "{\"mobile\":\"13800138000\",\"code\":\"123456\",\"email\":\"star_user@example.com\",\"username\":\"star_user\",\"password\":\"StarGraph@123\"}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "注册成功", content = @Content(schema = @Schema(implementation = Result.class))),
                    @ApiResponse(responseCode = "400", description = "参数校验失败、验证码错误、手机号已注册或用户名已存在", content = @Content(schema = @Schema(implementation = Result.class)))
            }
    )
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        userService.register(request);
        return Result.ok("注册成功", null);
    }

    /** 用户登录并返回 JWT。 */
    @PostMapping("/login")
    @Operation(
            summary = "用户登录",
            description = "支持用户名或手机号二选一登录，登录成功后返回 JWT。调用 ComfyUI 接口前可在 Knife4j 右上角 Authorize 中填入该 token。",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "登录请求体。username 与 mobile 至少填写一个，password 必填；若 username 和 mobile 同时存在，后端优先使用 username。",
                    content = @Content(
                            schema = @Schema(implementation = LoginRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "用户名登录",
                                            value = "{\"username\":\"star_user\",\"password\":\"StarGraph@123\"}"
                                    ),
                                    @ExampleObject(
                                            name = "手机号登录",
                                            value = "{\"mobile\":\"13800138000\",\"password\":\"StarGraph@123\"}"
                                    )
                            }
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "登录成功，返回 JWT", content = @Content(schema = @Schema(implementation = Result.class))),
                    @ApiResponse(responseCode = "400", description = "账号不存在、密码错误或账号状态异常", content = @Content(schema = @Schema(implementation = Result.class)))
            }
    )
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.ok(userService.login(request));
    }
}
