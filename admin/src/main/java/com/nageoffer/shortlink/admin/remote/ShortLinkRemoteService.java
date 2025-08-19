package com.nageoffer.shortlink.admin.remote;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.dto.req.RecycleBinPageReqDTO;
import com.nageoffer.shortlink.admin.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.*;
import com.nageoffer.shortlink.admin.remote.dto.resp.*;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 短链接中台远程调用服务
 * @author 20784
 */
public interface ShortLinkRemoteService {

    /**
     * 创建短链接 - 后管系统
     * @param requestParam 短链接创建参数
     * @return 创建响应
     */
    default Result<ShortLinkCreateRespDTO> createShortLink(ShortLinkCreateReqDTO requestParam){
        String resultBodyStr = HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/create", JSON.toJSONString(requestParam));
        return JSON.parseObject(resultBodyStr, new TypeReference<>(){});
    }

    /**
     *  分页查询短链接 - 后管系统
     * @param requestParam 分页查询参数
     * @return 分页查询响应结果
     */
    default IPage<ShortLinkPageRespDTO> pageShortlink(ShortLinkPageReqDTO requestParam){
        Map<String,Object> requestMap = new HashMap<>();
        requestMap.put("gid",requestParam.getGid());
        requestMap.put("current",requestParam.getCurrent());
        requestMap.put("size",requestParam.getSize());
        requestMap.put("orderTag",requestParam.getOrderTag());
        String resultPageStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/page", requestMap);
        Result<IPage<ShortLinkPageRespDTO>> resultObj =
                JSON.parseObject(resultPageStr, new TypeReference<Result<IPage<ShortLinkPageRespDTO>>>() {});
        return resultObj.getData();
    }

    /**
     * 查询分组短链接数量统计 - 后管系统
     * @param requestParam 请求参数 -gids
     */
    default List<ShortLinkGroupCountQueryRespDTO> listGroupShortLinkCount(List<String> requestParam){
        Map<String,Object> requestMap = new HashMap<>();
        requestMap.put("requestParam",requestParam);
        String resultJsonStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/count/remote", requestMap);
        return JSON.parseArray(resultJsonStr, ShortLinkGroupCountQueryRespDTO.class);
    }

    /**
     * 修改短链接 - 后管系统
     * @param requestParam 短链接修改参数
     */
    default void updateShortLink(ShortLinkUpdateReqDTO requestParam){
        System.out.println(requestParam.getOriginGid());
        String resultJsonStr = HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/update", JSON.toJSONString(requestParam));
    }

    /**
     * 根据原始链接获取标题
     * @param url 原始链接
     * @return 网站图片
     */
    default String getTitleByUrl(String url) throws IOException{
        return HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/title/remote?url=" + url);
    }

    /** 保存短链接到回收站
     * @param requestParam 请求参数
     */
    default void saveRecycleBin(RecycleBinSaveReqDTO requestParam){
        HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/recyclebin/save",JSON.toJSONString(requestParam));
    }

    /**
     * 分页查询回收站短链接
     * @param requestParam 分页参数
     * @return 分页结果
     */
    default IPage<ShortLinkPageRespDTO> pageRecycleBin(RecycleBinPageReqDTO requestParam){
        Map<String,Object> requestMap = new HashMap<>();
        requestMap.put("gidList",requestParam.getGidList());
        requestMap.put("current",requestParam.getCurrent());
        requestMap.put("size",requestParam.getSize());
        String resultPageStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/recyclebin/page", requestMap);
        Result<IPage<ShortLinkPageRespDTO>> resultObj =
                JSON.parseObject(resultPageStr, new TypeReference<Result<IPage<ShortLinkPageRespDTO>>>() {});
        return resultObj.getData();
    }

    /**
     * 从回收站中恢复短链接
     * @param requestParam 请求参数
     */
    default void recoverRecycleBin(RecycleBinRecoverReqDTO requestParam){
        HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/recyclebin/recover",JSON.toJSONString(requestParam));
    }

    /**
     * 从回收站中删除短链接
     * @param requestParam 请求参数
     */
    default void removeRecycleBin(RecycleBinRemoveReqDTO requestParam){
        HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/recyclebin/remove",JSON.toJSONString(requestParam));
    }

    /**
     * 获取单个短链接访问统计数据
     */
    default Result<ShortLinkStatsRespDTO> oneShortLinkStats(String fullShortUrl, String gid, Integer enableStatus, String startDate, String endDate){
        Map<String,Object> requestMap = new HashMap<>();
        requestMap.put("fullShortUrl",fullShortUrl);
        requestMap.put("gid",gid);
        requestMap.put("enableStatus",enableStatus);
        requestMap.put("startDate",startDate);
        requestMap.put("endDate",endDate);
        String resultBodyStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/stats", requestMap);
        return JSON.parseObject(resultBodyStr, new TypeReference<>(){});
    }

    default Result<ShortLinkStatsRespDTO> groupShortLinkStats(String gid, String startDate, String endDate){
        Map<String,Object> requestMap = new HashMap<>();
        requestMap.put("gid",gid);
        requestMap.put("startDate",startDate);
        requestMap.put("endDate",endDate);
        String resultBodyStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/stats/group", requestMap);
        return JSON.parseObject(resultBodyStr, new TypeReference<>(){});
    }

    /**
     * 访问单个短链接指定时间内访问记录监控数据 后管
     */
    default Result<Page<ShortLinkStatsAccessRecordRespDTO>> shortLinkStatsAccessRecord(String fullShortUrl, String gid, String startDate, String endDate, Integer enableStatus, long current, long size){
        Map<String,Object> requestMap = new HashMap<>();
        requestMap.put("fullShortUrl",fullShortUrl);
        requestMap.put("gid",gid);
        requestMap.put("enableStatus",enableStatus);
        requestMap.put("startDate",startDate);
        requestMap.put("endDate",endDate);
        requestMap.put("current",current);
        requestMap.put("size",size);
        String resultPageStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/stats/access-record",requestMap);
        return JSON.parseObject(resultPageStr, new TypeReference<>(){});
    }

    default Result<Page<ShortLinkStatsAccessRecordRespDTO>> groupShortLinkStatsAccessRecord(String gid, String startDate, String endDate, long current, long size){
        Map<String,Object> requestMap = new HashMap<>();
        requestMap.put("gid",gid);
        requestMap.put("startDate",startDate);
        requestMap.put("endDate",endDate);
        requestMap.put("current",current);
        requestMap.put("size",size);
        String resultPageStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/v1/stats/access-record/group",requestMap);
        return JSON.parseObject(resultPageStr, new TypeReference<>(){});
    }

    /**
     * 批量创建短链接
     * @param requestParam 批量创建参数
     * @return 批量创建返回参数
     */
    default Result<ShortLinkBatchCreateRespDTO> batchCreateShortLink(ShortLinkBatchCreateReqDTO requestParam){
        String resultBodyStr = HttpUtil.post("http://127.0.0.1:8001/api/short-link/v1/create/batch", JSON.toJSONString(requestParam));
        return JSON.parseObject(resultBodyStr, new TypeReference<>(){});
    }
}
