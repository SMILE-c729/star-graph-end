package com.stargraph.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stargraph.entity.TaskEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TaskMapper extends BaseMapper<TaskEntity> {
}
