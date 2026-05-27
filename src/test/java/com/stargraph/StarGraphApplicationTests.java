package com.stargraph;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Spring Boot 应用上下文测试。
 * 只验证容器能否启动，不直接调用外部 ComfyUI 服务，避免测试依赖本机运行环境。
 */
@SpringBootTest
class StarGraphApplicationTests {

    @Test
    void contextLoads() {
    }
}
