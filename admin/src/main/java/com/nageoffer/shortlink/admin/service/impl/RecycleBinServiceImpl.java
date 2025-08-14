package com.nageoffer.shortlink.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.nageoffer.shortlink.admin.common.biz.user.UserContext;
import com.nageoffer.shortlink.admin.common.convention.exception.ClientException;
import com.nageoffer.shortlink.admin.dao.entity.GroupDO;
import com.nageoffer.shortlink.admin.dao.mapper.GroupMapper;
import com.nageoffer.shortlink.admin.dto.req.ShortLinkRecycleBinReqDTO;
import com.nageoffer.shortlink.admin.remote.ShortLinkRemoteService;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import com.nageoffer.shortlink.admin.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 短链接回收站 -- 后管系统
 * @author 20784
 */
@Service
@RequiredArgsConstructor
public class RecycleBinServiceImpl implements RecycleBinService {

    private final GroupMapper groupMapper;

    @Override
    public IPage<ShortLinkPageRespDTO> pageRecycleBin(ShortLinkRecycleBinReqDTO requestParam) {
        //首先需要通过用户用户上下文获取当前用户的gid List
        String username = UserContext.getUsername();
        LambdaQueryWrapper<GroupDO>  queryWrapper = Wrappers.<GroupDO>lambdaQuery()
                .eq(GroupDO::getUsername, username)
                .eq(GroupDO::getDelFlag,0);
        //获取到当前username下所有启用
        List<String> gidList = groupMapper.selectList(queryWrapper).
                stream().map(GroupDO::getGid).toList();
        if(CollectionUtils.isEmpty(gidList)){
            throw new ClientException("当前用户暂无短链接分组!");
        }
        requestParam.setGidList(gidList);
        ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService() {
        };
        //远程调用方法
        return shortLinkRemoteService.pageRecycleBin(requestParam);
    }
}
