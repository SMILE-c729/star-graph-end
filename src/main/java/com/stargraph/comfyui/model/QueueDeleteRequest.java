package com.stargraph.comfyui.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * ComfyUI 队列删除请求体。
 * 对应 ComfyUI API 的 POST /queue 接口，用于从等待队列中移除指定任务。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueueDeleteRequest {

    /** 要从队列中删除的任务 prompt_id 列表 */
    private List<String> delete;
}
