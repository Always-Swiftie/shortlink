package com.nageoffer.shortlink.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;

import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.common.convention.result.Results;
import com.nageoffer.shortlink.admin.remote.ShortLinkRemoteService;
import com.nageoffer.shortlink.admin.remote.dto.req.ShortLinkCreateReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.ShortLinkPageReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.req.ShortLinkUpdateReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 短链接控制层 - 后管系统
 * @author 20784
 */
@RestController
@RequiredArgsConstructor
public class ShortLinkController {

    //TODO 后续需要重构为Spring Cloud 调用
    /**
     * 创建短链接 -- 后管
     * @param requestParam 短链接创建参数
     * @return 短链接创建返回参数
     */
    @PostMapping("/api/shortlink/v1/admin/create")
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam){
        ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService() {
        };
        return shortLinkRemoteService.createShortLink(requestParam);
    }


    /**
     * 分页查询短链接 -- 后管
     * @param requestParam 分页参数:gid
     *
     */
    @GetMapping("/api/shortlink/v1/admin/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam){
        ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService() {
        };
        return Results.success(shortLinkRemoteService.pageShortlink(requestParam));
    }

    /**
     * 修改短链接 -- 后管
     */
    @PostMapping("/api/shortlink/v1/admin/update")
    public Result<Void> updateShortLink(@RequestBody ShortLinkUpdateReqDTO requestParam){
        ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService() {
        };
        shortLinkRemoteService.updateShortLink(requestParam);
        return Results.success();
    }
}
