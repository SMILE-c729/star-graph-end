package com.stargraph.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码加密配置。
 * 统一暴露 PasswordEncoder Bean，避免业务类直接 new 加密实现，后续更换算法更方便。
 */
@Configuration
public class PasswordConfig {

    /**
     * BCrypt 是适合密码存储的单向哈希算法，内部会自动生成盐值。
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
