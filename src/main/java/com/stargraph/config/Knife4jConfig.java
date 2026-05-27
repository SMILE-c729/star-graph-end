package com.stargraph.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j / OpenAPI3 配置。
 * 访问地址：http://localhost:8080/doc.html
 */
@Configuration
public class Knife4jConfig {

    public static final String JWT_SECURITY_SCHEME = "JWT";

    @Bean
    public OpenAPI starGraphOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Star Graph 后端接口文档")
                        .description("提供用户账号与 ComfyUI 代理接口的在线调试能力。ComfyUI 接口需要先在右上角 Authorize 中填写登录返回的 JWT。")
                        .version("1.0.0")
                        .contact(new Contact().name("Star Graph")))
                .components(new Components().addSecuritySchemes(JWT_SECURITY_SCHEME,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("填写登录接口返回的 token，Knife4j 会自动以 Authorization: Bearer <token> 发送请求。")));
    }

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("用户账号接口")
                .pathsToMatch("/api/1.0/user/**")
                .build();
    }

    @Bean
    public GroupedOpenApi comfyUiGroupedOpenApi() {
        return GroupedOpenApi.builder()
                .group("ComfyUI接口")
                .pathsToMatch("/api/comfyui/**")
                .build();
    }
}
