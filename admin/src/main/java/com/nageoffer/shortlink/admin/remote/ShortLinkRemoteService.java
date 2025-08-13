package com.nageoffer.shortlink.admin.remote;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.alibaba.fastjson2.util.BeanUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.ShortLinkCreateReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.ShortLinkPageReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.ShortLinkUpdateReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;

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
        String resultBodyStr = HttpUtil.post("http://127.0.0.1:8001/api/shortlink/v1/create", JSON.toJSONString(requestParam));
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
        String resultPageStr = HttpUtil.get("http://127.0.0.1:8001/api/shortlink/v1/page", requestMap);
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
        String resultJsonStr = HttpUtil.get("http://127.0.0.1:8001/api/shortlink/v1/count/remote", requestMap);
        return JSON.parseArray(resultJsonStr, ShortLinkGroupCountQueryRespDTO.class);
    }

    /**
     * 修改短链接 - 后管系统
     * @param requestParam 短链接修改参数
     */
    default void updateShortLink(ShortLinkUpdateReqDTO requestParam){
        System.out.println(requestParam.getOriginGid());
        String resultJsonStr = HttpUtil.post("http://127.0.0.1:8001/api/shortlink/v1/update", JSON.toJSONString(requestParam));
    }

    default String getTitleByUrl(String url) throws IOException{
        return HttpUtil.get("http://127.0.0.1:8001/api/shortlink/v1/title/remote?url=" + url);
    }
}
