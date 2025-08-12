package com.nageoffer.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nageoffer.shortlink.project.dao.entity.ShortLinkDO;
import com.nageoffer.shortlink.project.dto.resp.ShortLinkGroupCountQueryRespDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * 短链接持久层实体
 * @author 20784
 */
@Mapper
public interface ShortLinkMapper extends BaseMapper<ShortLinkDO> {

    List<ShortLinkGroupCountQueryRespDTO> groupShortLinkCount(List<String> requestParam);

}
