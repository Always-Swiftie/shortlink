package com.nageoffer.shortlink.admin.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nageoffer.shortlink.admin.dao.entity.UserDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 20784
 * 用户持久层
 */
@Mapper
public interface UserMapper extends BaseMapper<UserDO> {
}
