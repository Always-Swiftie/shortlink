package com.nageoffer.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nageoffer.shortlink.admin.common.biz.user.UserContext;
import com.nageoffer.shortlink.admin.common.database.BaseDO;
import com.nageoffer.shortlink.admin.dao.entity.GroupDO;
import com.nageoffer.shortlink.admin.dao.mapper.GroupMapper;
import com.nageoffer.shortlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import com.nageoffer.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;
import com.nageoffer.shortlink.admin.service.GroupService;
import com.nageoffer.shortlink.admin.util.CodeGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 短链接分组接口实现层
 * @author 20784
 */
@Service
@Slf4j
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {

    @Override
    public void save(String groupName) {
        String gid;
        do {
            gid = CodeGenerator.generateRandomCode();
        } while (hasGid(gid));

        GroupDO groupDO = GroupDO.builder()
                .name(groupName)
                .gid(gid)
                .sortOrder(0)
                .username(UserContext.getUsername())
                .build();
        baseMapper.insert(groupDO);
    }

    private boolean hasGid(String gid) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.<GroupDO>lambdaQuery()
                .eq(GroupDO::getGid, gid)
                //TODO 通过Gateway 获取用户名
                .eq(GroupDO::getUsername,null);
        GroupDO groupDO = baseMapper.selectOne(queryWrapper);
        return groupDO != null;
    }

    @Override
    public List<ShortLinkGroupRespDTO> listGroup() {
        //TODO 获取当前用户名
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.<GroupDO>lambdaQuery()
                .eq(BaseDO::getDelFlag,0)
                .eq(GroupDO::getUsername,UserContext.getUsername())
                .orderByDesc(GroupDO::getSortOrder,GroupDO::getUpdateTime);
        List<GroupDO> groupDOList = baseMapper.selectList(queryWrapper);
        return BeanUtil.copyToList(groupDOList, ShortLinkGroupRespDTO.class);
    }

    @Override
    public void updateGroup(ShortLinkGroupUpdateReqDTO requestParam) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.<GroupDO>lambdaQuery()
                .eq(GroupDO::getGid, requestParam.getGid())
                .eq(GroupDO::getUsername,UserContext.getUsername())
                .eq(GroupDO::getDelFlag,0);
        GroupDO groupDO = GroupDO.builder()
                .name(requestParam.getName())
                .build();
        baseMapper.update(groupDO,queryWrapper);
    }

    @Override
    public void deleteGroup(String gid) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.<GroupDO>lambdaQuery()
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getUsername,UserContext.getUsername())
                .eq(GroupDO::getDelFlag,0);
        GroupDO groupDO = baseMapper.selectOne(queryWrapper);
        groupDO.setDelFlag(1);
        baseMapper.update(groupDO,queryWrapper);
    }

}
