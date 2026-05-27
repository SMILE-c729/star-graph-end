package com.stargraph.comfyui.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

/**
 * 提交 ComfyUI 任务的请求 DTO。
 * 用于 Controller 层参数校验，通过 @Valid 注解触发 Jakarta Validation 校验规则。
 */
@Data
public class SubmitTaskRequest {

    /** 兼容旧前端字段。实际提交到 ComfyUI 时后端会统一使用自己的 WebSocket clientId */
    private String clientId;

    /** 工作流节点定义，结构与 ComfyUI API 的 prompt 字段一致 */
    @NotNull(message = "prompt 不能为空")
    private Map<String, Object> prompt;
}
