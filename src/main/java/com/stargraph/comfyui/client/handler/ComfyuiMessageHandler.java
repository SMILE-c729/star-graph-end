package com.stargraph.comfyui.client.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
public class ComfyuiMessageHandler extends TextWebSocketHandler {

    private volatile WebSocketSession session;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        this.session = session;
        log.info("=============连接 ComfyUI WebSocket 成功: sessionId={}", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        log.info("=============收到 ComfyUI 消息: {}", message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        if (this.session != null && this.session.getId().equals(session.getId())) {
            this.session = null;
        }
        log.info("=============ComfyUI WebSocket 已关闭: sessionId={}, status={}", session.getId(), status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.warn("=============ComfyUI WebSocket 异常: sessionId={}, error={}",
                session.getId(), exception.getMessage());
    }

    public boolean isConnected() {
        return session != null && session.isOpen();
    }

    public String getSessionId() {
        return session == null ? null : session.getId();
    }
}
