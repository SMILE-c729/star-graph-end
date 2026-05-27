package com.stargraph.comfyui.client.config;

import com.stargraph.comfyui.client.handler.ComfyuiMessageHandler;
import com.stargraph.comfyui.properties.ComfyUiProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Configuration
public class ComfyuiConfig {

    @Bean
    public ComfyuiMessageHandler comfyuiMessageHandler() {
        return new ComfyuiMessageHandler();
    }
    /**
     * WebSocket连接管理器
     */
    @Bean(destroyMethod = "stop")
    public WebSocketConnectionManager webSocketConnectionManager(ComfyuiMessageHandler comfyuiMessageHandler,
                                                                 ComfyUiProperties comfyUiProperties) {
        WebSocketClient client = new StandardWebSocketClient();
        String url = buildWebSocketUrl(comfyUiProperties);
        WebSocketConnectionManager manager = new WebSocketConnectionManager(client, comfyuiMessageHandler, url);
        manager.start();
        return manager;
    }

    private String buildWebSocketUrl(ComfyUiProperties properties) {
        String wsUrl = properties.getWsUrl();
        String baseUrl = wsUrl.endsWith("/") ? wsUrl.substring(0, wsUrl.length() - 1) : wsUrl;
        if (baseUrl.contains("?clientId=") || baseUrl.contains("&clientId=")) {
            return baseUrl;
        }

        String clientId = URLEncoder.encode(properties.getClientId(), StandardCharsets.UTF_8);
        if (baseUrl.endsWith("/ws")) {
            return baseUrl + "?clientId=" + clientId;
        }
        return baseUrl + "/ws?clientId=" + clientId;
    }
}
