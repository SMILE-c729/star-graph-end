package com.stargraph.comfyui.model;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * ComfyUI 提交任务请求体。
 * 对应 ComfyUI API 的 POST /prompt 接口。
 * client_id 用于标识调用方，prompt 包含工作流节点定义（ComfyUI 的 node graph JSON）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromptRequest {

    /** 调用方唯一标识，用于 ComfyUI 区分不同客户端 */
    @SerializedName("client_id")
    private String clientId;

    /** 工作流节点定义，key 为节点 ID，value 为节点配置（class_type、inputs 等） */
    private Map<String, Object> prompt;
}
