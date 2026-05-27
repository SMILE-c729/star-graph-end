package com.stargraph;

import com.stargraph.comfyui.properties.ComfyUiProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Star Graph 应用启动类。
 * 通过 @MapperScan 扫描各业务域下的 MyBatis-Plus Mapper 接口。
 * 通过 @EnableConfigurationProperties 启用 @ConfigurationProperties 注解的 Bean 注册（如 ComfyUiProperties）。
 */
@SpringBootApplication
@MapperScan({"com.stargraph.user.mapper", "com.stargraph.comfyui.mapper"})
@EnableConfigurationProperties(ComfyUiProperties.class)
public class StarGraphApplication {

    public static void main(String[] args) {
        SpringApplication.run(StarGraphApplication.class, args);
    }
}
