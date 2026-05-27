package com.stargraph.comfyui.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ComfyUI 任务持久化实体，映射到 comfyui_task 表。
 * 用于记录提交到 ComfyUI 的任务状态和结果，支持通过 MyBatis-Plus BaseMapper 进行 CRUD 操作。
 * 当前已定义但 Controller 层尚未使用，属于预留的数据持久化层。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("comfyui_task")
public class TaskEntity {

    /** 主键，自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** ComfyUI 返回的任务唯一标识 */
    private String promptId;

    /** 调用方客户端标识 */
    private String clientId;

    /** 任务状态（如 pending、running、completed、failed） */
    private String status;

    /** 工作流 JSON 定义，存储提交时的完整 prompt */
    @TableField("workflow_json")
    private String workflowJson;

    /** 生成结果图片的文件名列表（JSON 数组格式） */
    @TableField("result_images")
    private String resultImages;

    /** 创建时间，插入时自动填充 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间，插入和更新时自动填充 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /** 逻辑删除标记：0 未删除，1 已删除。MyBatis-Plus @TableLogic 自动在查询时追加 deleted=0 条件 */
    @TableLogic
    private Integer deleted;
}
