package com.stargraph;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@MapperScan("com.stargraph.mapper")
@EnableConfigurationProperties
public class StarGraphApplication {

    public static void main(String[] args) {
        SpringApplication.run(StarGraphApplication.class, args);
    }
}
