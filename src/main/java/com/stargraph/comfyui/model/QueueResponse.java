package com.stargraph.comfyui.model;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * ComfyUI 队列状态响应体。
 * queue_running 为当前正在执行的任务，queue_pending 为等待执行的任务。
 * 每个任务以 [number, prompt_id, prompt_json, extra_data] 形式表示。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueueResponse {

    /** 正在执行的任务列表，通常最多 1 个 */
    @SerializedName("queue_running")
    private List<List<Object>> queueRunning;

    /** 等待执行的任务列表，按提交顺序排列 */
    @SerializedName("queue_pending")
    private List<List<Object>> queuePending;
}
