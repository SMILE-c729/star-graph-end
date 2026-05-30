package com.stargraph.user.controller;

import com.stargraph.common.response.Result;
import com.stargraph.user.dto.LoginRequest;
import com.stargraph.user.dto.LoginResponse;
import com.stargraph.user.dto.RegisterRequest;
import com.stargraph.user.dto.RegisterResponse;
import com.stargraph.user.dto.SendCodeRequest;
import com.stargraph.user.dto.SendCodeResponse;
import com.stargraph.user.dto.UserProfileResponse;
import com.stargraph.user.dto.UpdateProfileRequest;
import com.stargraph.user.dto.ChangePasswordRequest;
import com.stargraph.user.dto.AvatarUploadResponse;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
    public Result<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        return Result.ok("注册成功", userService.register(request));
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

    /** 获取当前登录用户资料。 */
    @GetMapping("/profile")
    @Operation(summary = "获取当前用户资料", description = "根据当前登录 token 返回用户资料和页面展示名称。")
    public Result<UserProfileResponse> getProfile() {
        return Result.ok(userService.getProfile());
    }

    /** 修改当前登录用户资料。 */
    @PutMapping("/profile")
    @Operation(summary = "修改当前用户资料", description = "支持修改用户名、邮箱、昵称、性别和头像地址。")
    public Result<UserProfileResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        return Result.ok("资料修改成功", userService.updateProfile(request));
    }

    /** 修改当前登录用户密码。 */
    @PutMapping("/password")
    @Operation(summary = "修改当前用户密码", description = "校验旧密码后更新为新密码。")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(request);
        return Result.ok("密码修改成功", null);
    }

    /** 上传当前登录用户头像。 */
    @PostMapping("/avatar")
    @Operation(summary = "上传当前用户头像", description = "上传图片到 MinIO 公共桶，并保存头像公共访问地址。")
    public Result<AvatarUploadResponse> uploadAvatar(@RequestParam("file") MultipartFile file) throws Exception {
        return Result.ok("头像上传成功", userService.uploadAvatar(file));
    }
}
