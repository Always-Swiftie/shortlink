package com.nageoffer.shortlink.admin.controller;

import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.common.convention.result.Results;
import com.nageoffer.shortlink.admin.remote.ShortLinkRemoteService;
import com.nageoffer.shortlink.admin.remote.dto.req.RecycleBinSaveReqDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 回收站管理控制层 -- 后管系统
 * @author 20784
 */
@RestController
@RequiredArgsConstructor
public class RecycleBinController {

    /**
     * 保存短链接到回收站
     * @param requestParam 请求参数
     *
     */
    @PostMapping("/api/shortlink/v1/admin/recyclebin/save")
    public Result<Void> saveRecycleBin(@RequestBody RecycleBinSaveReqDTO requestParam){
        ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService() {
        };
        shortLinkRemoteService.saveRecycleBin(requestParam);
        return Results.success();
    }


}
