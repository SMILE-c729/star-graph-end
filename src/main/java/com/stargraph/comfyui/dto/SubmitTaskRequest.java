package com.stargraph.comfyui.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

/**
 * 提交 ComfyUI 任务的请求 DTO。
 * 用于 Controller 层参数校验，通过 @Valid 注解触发 Jakarta Validation 校验规则。
 */
@Data
@Schema(name = "SubmitTaskRequest", description = "提交 ComfyUI 工作流任务的请求体。")
public class SubmitTaskRequest {

    /** 兼容旧前端字段。实际提交到 ComfyUI 时后端会统一使用自己的 WebSocket clientId */
    @Schema(description = "兼容旧前端的客户端标识字段；实际提交 ComfyUI 时后端会使用配置中的 clientId。", example = "star-graph")
    private String clientId;

    /** 工作流节点定义，结构与 ComfyUI API 的 prompt 字段一致 */
    @NotNull(message = "prompt 不能为空")
    @Schema(
            description = "ComfyUI 工作流节点定义；key 为节点 ID，value 中通常包含 class_type、inputs 等字段。",
            example = "{\"3\":{\"class_type\":\"KSampler\",\"inputs\":{\"seed\":123456,\"steps\":20,\"cfg\":8,\"sampler_name\":\"euler\",\"scheduler\":\"normal\",\"denoise\":1}},\"6\":{\"class_type\":\"CLIPTextEncode\",\"inputs\":{\"text\":\"a cinematic star graph, high detail\"}}}",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Map<String, Object> prompt;
}
