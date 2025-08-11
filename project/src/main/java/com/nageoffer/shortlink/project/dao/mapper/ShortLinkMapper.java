package com.nageoffer.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nageoffer.shortlink.project.dao.entity.ShortLinkDO;
import org.apache.ibatis.annotations.Mapper;


/**
 * 短链接持久层实体
 * @author 20784
 */
@Mapper
public interface ShortLinkMapper extends BaseMapper<ShortLinkDO> {
}
