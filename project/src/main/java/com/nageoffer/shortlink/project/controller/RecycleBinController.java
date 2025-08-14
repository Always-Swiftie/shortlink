package com.nageoffer.shortlink.project.controller;

import com.nageoffer.shortlink.project.common.convention.result.Result;
import com.nageoffer.shortlink.project.common.convention.result.Results;
import com.nageoffer.shortlink.project.dto.req.RecycleBinSaveReqDTO;
import com.nageoffer.shortlink.project.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 回收站管理控制层
 * @author 20784
 */
@RestController
@RequiredArgsConstructor
public class RecycleBinController {

    private final RecycleBinService recycleBinService;

    /**
     * 保存短链接到回收站
     * @param requestParam 请求参数
     *
     */
    @PostMapping("/api/shortlink/v1/recyclebin/save")
    public Result<Void> saveRecycleBin(@RequestBody RecycleBinSaveReqDTO requestParam){
        recycleBinService.saveRecycleBin(requestParam);
        return Results.success();
    }
}
