package com.stargraph.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stargraph.user.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表 MyBatis-Plus Mapper。
 * 继承 BaseMapper 获得用户表的标准 CRUD 能力。
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
}
