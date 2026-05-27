package com.stargraph.auth;

import cn.hutool.jwt.JWTUtil;
import com.stargraph.user.entity.UserEntity;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * JwtTokenUtil 单元测试。
 * 验证令牌生成、解析和非法令牌拒绝逻辑。
 */
class JwtTokenUtilTest {

    @Test
    void createTokenShouldCarryUserInfoAndExpireIn72Hours() {
        UserEntity user = UserEntity.builder()
                .id(1001L)
                .username("star_user")
                .build();

        long before = System.currentTimeMillis();
        String token = JwtTokenUtil.createToken(user);
        long after = System.currentTimeMillis();

        LoginUser loginUser = JwtTokenUtil.parseToken(token);
        long expireTime = ((Number) JWTUtil.parseToken(token)
                .getPayload("expireTime")).longValue();

        assertThat(loginUser.getId()).isEqualTo(1001L);
        assertThat(loginUser.getUsername()).isEqualTo("star_user");
        assertThat(expireTime).isBetween(
                before + Duration.ofHours(72).toMillis(),
                after + Duration.ofHours(72).toMillis()
        );
    }

    @Test
    void parseTokenShouldRejectBlankToken() {
        assertThatThrownBy(() -> JwtTokenUtil.parseToken(" "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("令牌不能为空");
    }

    @Test
    void parseTokenShouldRejectMalformedToken() {
        assertThatThrownBy(() -> JwtTokenUtil.parseToken("bad.token"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("令牌无效");
    }
}
