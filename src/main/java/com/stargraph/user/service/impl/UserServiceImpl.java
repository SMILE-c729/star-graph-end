package com.stargraph.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.stargraph.auth.JwtTokenUtil;
import com.stargraph.common.constant.StarGraphConstant;
import com.stargraph.user.dto.LoginRequest;
import com.stargraph.user.dto.LoginResponse;
import com.stargraph.user.dto.RegisterRequest;
import com.stargraph.user.dto.SendCodeResponse;
import com.stargraph.user.entity.UserEntity;
import com.stargraph.user.mapper.UserMapper;
import com.stargraph.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 用户账号业务实现。
 * 负责注册验证码校验、用户唯一性校验、密码加密和登录令牌签发。
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final PasswordEncoder passwordEncoder;

    @Override
    public SendCodeResponse sendCode(String mobile) {
        // 校验手机号格式并去重
        String normalizedMobile = normalize(mobile);
        if (existsByMobile(normalizedMobile)) {
            throw new IllegalArgumentException(StarGraphConstant.ErrorMessage.User.MOBILE_REGISTERED);
        }

        // 验证码只写入 Redis 并设置短 TTL，注册成功后会主动删除，避免重复使用。
        String code = generateVerificationCode();
        stringRedisTemplate.opsForValue().set(codeKey(normalizedMobile), code,
                Duration.ofSeconds(StarGraphConstant.VerificationCode.TTL_SECONDS));
        return new SendCodeResponse(code, StarGraphConstant.VerificationCode.TTL_SECONDS);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterRequest request) {
        String mobile = normalize(request.getMobile());
        String username = normalize(request.getUsername());

        if (existsByMobile(mobile)) {
            throw new IllegalArgumentException(StarGraphConstant.ErrorMessage.User.MOBILE_REGISTERED);
        }
        if (existsByUsername(username)) {
            throw new IllegalArgumentException(StarGraphConstant.ErrorMessage.User.USERNAME_EXISTS);
        }

        // 先校验验证码再写库，避免无效注册请求产生脏数据。
        verifyRegisterCode(mobile, normalize(request.getCode()));

        UserEntity user = buildNewUser(request, mobile, username);
        userMapper.insert(user);
        stringRedisTemplate.delete(codeKey(mobile));
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        String loginAccount = request.getLoginAccount();
        UserEntity user = selectByLoginAccount(loginAccount);
        if (user == null) {
            throw new IllegalArgumentException(StarGraphConstant.ErrorMessage.User.USER_NOT_FOUND);
        }
        if (!Integer.valueOf(StarGraphConstant.UserStatus.NORMAL).equals(user.getStatus())) {
            throw new IllegalArgumentException(resolveStatusMessage(user.getStatus()));
        }
        // BCrypt 每个密码有独立盐值，必须使用 matches 进行校验，不能直接比较密文。
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException(StarGraphConstant.ErrorMessage.User.PASSWORD_INCORRECT);
        }

        return new LoginResponse(user.getId(), resolveDisplayName(user),
                user.getAvatar() != null && !user.getAvatar().isBlank()
                        ? user.getAvatar()
                        : StarGraphConstant.UserDefault.AVATAR,
                JwtTokenUtil.createToken(user));
    }

    private UserEntity selectByLoginAccount(String loginAccount) {
        return userMapper.selectOne(new LambdaQueryWrapper<UserEntity>()
                .and(wrapper -> wrapper.eq(UserEntity::getMobile, loginAccount)
                        .or()
                        .eq(UserEntity::getUsername, loginAccount))
                .last("limit 1"));
    }

    private UserEntity buildNewUser(RegisterRequest request, String mobile, String username) {
        LocalDateTime now = LocalDateTime.now();
        return UserEntity.builder()
                .createTime(now)
                .updateTime(now)
                .mobile(mobile)
                .password(passwordEncoder.encode(request.getPassword()))
                .status(StarGraphConstant.UserStatus.NORMAL)
                .username(username)
                .email(normalize(request.getEmail()))
                .vipLevel(StarGraphConstant.UserDefault.VIP_LEVEL)
                .deleted(StarGraphConstant.LogicDelete.NOT_DELETED)
                .nickname(username)
                .avatar(StarGraphConstant.UserDefault.AVATAR)
                .gender(StarGraphConstant.UserDefault.UNKNOWN_GENDER)
                .build();
    }

    private void verifyRegisterCode(String mobile, String inputCode) {
        String cacheCode = stringRedisTemplate.opsForValue().get(codeKey(mobile));
        if (cacheCode == null) {
            throw new IllegalArgumentException(StarGraphConstant.ErrorMessage.User.CODE_EXPIRED);
        }
        if (!cacheCode.equals(inputCode)) {
            throw new IllegalArgumentException(StarGraphConstant.ErrorMessage.User.CODE_INCORRECT);
        }
    }

    private boolean existsByMobile(String mobile) {
        return userMapper.selectCount(new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getMobile, mobile)) > 0;
    }

    private boolean existsByUsername(String username) {
        return userMapper.selectCount(new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getUsername, username)) > 0;
    }

    private String codeKey(String mobile) {
        return StarGraphConstant.VerificationCode.REDIS_KEY_PREFIX + mobile;
    }

    private String generateVerificationCode() {
        // 生成六位随机数验证码
        return String.format(StarGraphConstant.VerificationCode.SIX_DIGIT_FORMAT,
                ThreadLocalRandom.current().nextInt(StarGraphConstant.VerificationCode.RANDOM_BOUND));
    }

    private String normalize(String value) {
        // 去除空格
        return value == null ? null : value.trim();
    }

    private String resolveDisplayName(UserEntity user) {
        if (user.getNickname() != null && !user.getNickname().isBlank()) {
            return user.getNickname();
        }
        return user.getUsername();
    }

    private String resolveStatusMessage(Integer status) {
        if (status == null) {
            return StarGraphConstant.ErrorMessage.User.ACCOUNT_STATUS_ERROR;
        }
        return switch (status) {
            case StarGraphConstant.UserStatus.TIMEOUT_LOCKED -> StarGraphConstant.ErrorMessage.User.ACCOUNT_TIMEOUT_LOCKED;
            case StarGraphConstant.UserStatus.LOCKED -> StarGraphConstant.ErrorMessage.User.ACCOUNT_LOCKED;
            case StarGraphConstant.UserStatus.DISABLED -> StarGraphConstant.ErrorMessage.User.ACCOUNT_DISABLED;
            default -> StarGraphConstant.ErrorMessage.User.ACCOUNT_STATUS_ERROR;
        };
    }
}
