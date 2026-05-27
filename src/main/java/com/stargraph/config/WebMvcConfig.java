package com.stargraph.config;

import com.stargraph.auth.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 配置。
 * 注册登录认证拦截器，并放行注册、登录、验证码等公开接口。
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 所有 /api/** 默认需要认证，用户登录注册相关接口作为白名单放行。
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/1.0/user/code",
                        "/api/1.0/user/register",
                        "/api/1.0/user/login"
                );
    }
}
