package com.nageoffer.shortlink.admin.controller;

import com.nageoffer.shortlink.admin.remote.ShortLinkRemoteService;
import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.common.convention.result.Results;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * URL标题控制层
 * @author 20784
 */
@RestController
@RequiredArgsConstructor
public class UrlTitleController {

    /**
     * 根据URL获取对应网站的标题
     * @param url
     * @return
     */
    @GetMapping("/api/short-link/admin/v1/title")
    public Result<String> getTitleByUrl(@RequestParam String url) throws IOException {
        ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService() {
        };
        String title = shortLinkRemoteService.getTitleByUrl(url);
        return Results.success(title);
    }


}
