package com.stargraph.comfyui.model;

import com.google.gson.annotations.SerializedName;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "PromptResponse", description = "ComfyUI 提交任务响应体。")
public class PromptResponse {

    /** 任务唯一标识，后续可通过 /history/{prompt_id} 查询结果 */
    @SerializedName("prompt_id")
    @Schema(description = "任务唯一标识，后续可通过历史记录接口查询结果。", example = "2f7a4d51-8c0e-4c2d-a4ef-1b2c3d4e5f60")
    private String promptId;

    /** 任务在队列中的编号 */
    @Schema(description = "任务在队列中的编号。", example = "12")
    private Long number;

    /** 提交失败时的错误信息列表，成功时为空 */
    @Schema(description = "提交失败时的错误信息列表；成功时通常为空。", example = "[]")
    private List<String> errors;
}
