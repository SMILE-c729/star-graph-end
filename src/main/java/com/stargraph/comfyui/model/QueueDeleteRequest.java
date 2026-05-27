package com.stargraph.comfyui.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "QueueDeleteRequest", description = "ComfyUI 队列删除请求体。")
public class QueueDeleteRequest {

    /** 要从队列中删除的任务 prompt_id 列表 */
    @ArraySchema(
            arraySchema = @Schema(description = "要从等待队列中删除的 prompt_id 列表。"),
            schema = @Schema(description = "任务 prompt_id。", example = "2f7a4d51-8c0e-4c2d-a4ef-1b2c3d4e5f60")
    )
    private List<String> delete;
}
