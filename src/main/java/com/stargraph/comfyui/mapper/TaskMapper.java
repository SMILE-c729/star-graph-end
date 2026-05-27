package com.stargraph.comfyui.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stargraph.comfyui.entity.TaskEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务表 MyBatis-Plus Mapper 接口。
 * 继承 BaseMapper 获得标准 CRUD 能力（insert、selectById、updateById、deleteById 等）。
 * 无需编写 XML，MyBatis-Plus 自动生成 SQL。
 */
@Mapper
public interface TaskMapper extends BaseMapper<TaskEntity> {
}
