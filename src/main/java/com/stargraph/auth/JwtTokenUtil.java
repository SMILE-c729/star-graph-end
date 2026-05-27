package com.stargraph.auth;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import com.stargraph.common.constant.StarGraphConstant;
import com.stargraph.user.entity.UserEntity;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 令牌工具类。
 * 负责创建登录令牌和解析校验令牌，是当前轻量认证机制的核心入口。
 */
public final class JwtTokenUtil {

    private static final byte[] JWT_KEY = StarGraphConstant.JwtConfig.SECRET.getBytes(StandardCharsets.UTF_8);

    private JwtTokenUtil() {
    }

    /**
     * 根据用户实体创建登录令牌。
     * payload 同时写入业务过期时间和标准 exp 字段，便于后续兼容其他 JWT 工具。
     */
    public static String createToken(UserEntity user) {
        long now = System.currentTimeMillis();
        long expireTime = now + StarGraphConstant.JwtConfig.TOKEN_TTL.toMillis();
        Map<String, Object> payload = new HashMap<>();
        payload.put(StarGraphConstant.JwtClaim.ID, user.getId());
        payload.put(StarGraphConstant.JwtClaim.USERNAME, user.getUsername());
        payload.put(StarGraphConstant.JwtClaim.ISSUED_AT, now);
        payload.put(StarGraphConstant.JwtClaim.EXPIRE_TIME, expireTime);
        payload.put(StarGraphConstant.JwtClaim.IAT, now / 1000);
        payload.put(StarGraphConstant.JwtClaim.EXP, expireTime / 1000);
        return JWTUtil.createToken(payload, JWT_KEY);
    }

    /**
     * 解析并校验登录令牌。
     * 校验失败统一抛出 IllegalArgumentException，由认证拦截器转换为 401 响应。
     */
    public static LoginUser parseToken(String token) {
        if (!StringUtils.hasText(token)) {
            throw new IllegalArgumentException(StarGraphConstant.ErrorMessage.Token.BLANK);
        }
        JWT jwt = parseVerifiedJwt(token);
        long expireTime = parseLong(jwt.getPayload(StarGraphConstant.JwtClaim.EXPIRE_TIME),
                StarGraphConstant.ErrorMessage.Token.MISSING_EXPIRE_TIME);
        if (expireTime < System.currentTimeMillis()) {
            throw new IllegalArgumentException(StarGraphConstant.ErrorMessage.Token.EXPIRED);
        }

        Long userId = parseLong(jwt.getPayload(StarGraphConstant.JwtClaim.ID),
                StarGraphConstant.ErrorMessage.Token.MISSING_USER_ID);
        String username = String.valueOf(jwt.getPayload(StarGraphConstant.JwtClaim.USERNAME));
        if (!StringUtils.hasText(username) || "null".equals(username)) {
            throw new IllegalArgumentException(StarGraphConstant.ErrorMessage.Token.MISSING_USERNAME);
        }
        return new LoginUser(userId, username);
    }

    private static JWT parseVerifiedJwt(String token) {
        try {
            // 先验签再解析，避免未签名或被篡改的 payload 进入业务逻辑。
            if (!JWTUtil.verify(token, JWT_KEY)) {
                throw new IllegalArgumentException(StarGraphConstant.ErrorMessage.Token.INVALID);
            }
            return JWTUtil.parseToken(token);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException(StarGraphConstant.ErrorMessage.Token.INVALID);
        }
    }
    /**
     * 解析 Long 类型的 payload 值。
     * payload 值可能为 Number 类型或 String 类型，需要统一转换为 Long 类型。
     */
    private static Long parseLong(Object value, String errorMessage) {
        if (value == null) {
            throw new IllegalArgumentException(errorMessage);
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
