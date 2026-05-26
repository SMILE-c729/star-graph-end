package com.stargraph.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("comfyui_task")
public class TaskEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String promptId;

    private String clientId;

    private String status;

    @TableField("workflow_json")
    private String workflowJson;

    @TableField("result_images")
    private String resultImages;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
