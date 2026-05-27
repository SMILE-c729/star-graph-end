package com.stargraph.comfyui.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * ComfyUI 配置属性类。
 * 绑定 application.yaml 中 comfyui 前缀的配置项。
 * 不需要 @Component，因为主类的 @EnableConfigurationProperties 已自动注册该 Bean。
 */
@Data
@ConfigurationProperties(prefix = "comfyui")
public class ComfyUiProperties {

    /** ComfyUI 服务器地址，默认本地 8000 端口 */
    private String baseUrl = "http://localhost:8000";

    /** ComfyUI WebSocket 地址，默认本地 8000 端口 */
    private String wsUrl = "ws://localhost:8000";

    /** ComfyUI WebSocket 与提交任务共用的客户端标识 */
    private String clientId = "star-graph";
}
