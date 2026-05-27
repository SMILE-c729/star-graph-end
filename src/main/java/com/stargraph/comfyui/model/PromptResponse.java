package com.stargraph.comfyui.model;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * ComfyUI 提交任务响应体。
 * prompt_id 是任务唯一标识，可用于查询历史记录和结果。
 * number 为队列中的位置编号，errors 非空时表示任务提交失败。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromptResponse {

    /** 任务唯一标识，后续可通过 /history/{prompt_id} 查询结果 */
    @SerializedName("prompt_id")
    private String promptId;

    /** 任务在队列中的编号 */
    private Long number;

    /** 提交失败时的错误信息列表，成功时为空 */
    private List<String> errors;
}
