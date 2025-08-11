package com.nageoffer.shortlink.admin.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nageoffer.shortlink.admin.dao.entity.GroupDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 短链接分组持久层接口
 * @author 20784
 */
@Mapper
public interface GroupMapper extends BaseMapper<GroupDO> {
}
