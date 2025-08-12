package com.nageoffer.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nageoffer.shortlink.admin.common.biz.user.UserContext;
import com.nageoffer.shortlink.admin.common.database.BaseDO;
import com.nageoffer.shortlink.admin.dao.entity.GroupDO;
import com.nageoffer.shortlink.admin.dao.mapper.GroupMapper;
import com.nageoffer.shortlink.admin.dto.req.ShortLinkGroupSortReqDTO;
import com.nageoffer.shortlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import com.nageoffer.shortlink.admin.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.nageoffer.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;
import com.nageoffer.shortlink.admin.remote.ShortLinkRemoteService;
import com.nageoffer.shortlink.admin.service.GroupService;
import com.nageoffer.shortlink.admin.util.CodeGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 短链接分组接口实现层
 * @author 20784
 */
@Service
@Slf4j
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {

    ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService() {
    };

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
                .eq(GroupDO::getUsername,UserContext.getUsername());
        GroupDO groupDO = baseMapper.selectOne(queryWrapper);
        return groupDO != null;
    }

    @Override
    public List<ShortLinkGroupRespDTO> listGroup() {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.<GroupDO>lambdaQuery()
                .eq(BaseDO::getDelFlag,0)
                .eq(GroupDO::getUsername,UserContext.getUsername())
                .orderByDesc(GroupDO::getSortOrder,GroupDO::getUpdateTime);
        List<GroupDO> groupDOList = baseMapper.selectList(queryWrapper);
        // TODO 通过已经完成的接口,为每一个GroupRespDTO 的shortLinkCount属性赋值
        // 先拿到所有的gid,构建为gid 的list,作为下面service方法的请求参数
        List<String> gids = groupDOList.stream().map(GroupDO::getGid).toList();
        // 拿到了每个gid 及其对应的数量,需要取出来
        List<ShortLinkGroupCountQueryRespDTO> groupCountList = shortLinkRemoteService.listGroupShortLinkCount(gids);
        // 为CountList构建一个HashMap,方便使用
        Map<String,Integer> groupCountMap = new HashMap<>();
        for(ShortLinkGroupCountQueryRespDTO item:groupCountList){
            String gid = item.getGid();
            Integer count = item.getShortLinkCount();
            groupCountMap.put(gid,count);
        }
        //HashMap 构建完毕
        List<ShortLinkGroupRespDTO> resultList = BeanUtil.copyToList(groupDOList, ShortLinkGroupRespDTO.class);
        resultList.forEach(each -> {
            each.setShortLinkCount(groupCountMap.get(each.getGid()));
        });
        //赋值完毕
        return resultList;
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
        String username = UserContext.getUsername();
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.<GroupDO>lambdaQuery()
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getUsername,UserContext.getUsername())
                .eq(GroupDO::getDelFlag,0);
        GroupDO groupDO = baseMapper.selectOne(queryWrapper);
        groupDO.setDelFlag(1);
        baseMapper.update(groupDO,queryWrapper);
    }

    @Override
    public void sortGroup(List<ShortLinkGroupSortReqDTO> requestParam) {
        String username = UserContext.getUsername();
        requestParam.forEach(dto -> {
            GroupDO groupDO = GroupDO.builder()
                    .gid(dto.getGid())
                    .sortOrder(dto.getSortOrder())
                    .build();
            LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.<GroupDO>lambdaQuery()
                    .eq(GroupDO::getGid, dto.getGid())
                    .eq(GroupDO::getUsername,UserContext.getUsername())
                    .eq(GroupDO::getDelFlag,0);
            baseMapper.update(groupDO,queryWrapper);
        });
    }
}
