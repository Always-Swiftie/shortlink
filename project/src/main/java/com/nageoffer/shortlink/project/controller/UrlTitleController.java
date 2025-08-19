package com.nageoffer.shortlink.project.controller;

import com.nageoffer.shortlink.project.common.convention.result.Result;
import com.nageoffer.shortlink.project.common.convention.result.Results;
import com.nageoffer.shortlink.project.service.UrlTitleService;
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

    private final UrlTitleService urlTitleService;

    /**
     * 根据URL获取对应网站的标题
     * @param url
     * @return
     */
    @GetMapping("/api/short-link/v1/title")
    public Result<String> getTitleByUrl(@RequestParam String url) throws IOException {
        return Results.success(urlTitleService.getTitleByUrl(url));
    }

    @GetMapping("/api/short-link/v1/title/remote")
    public String getTitleByUrlRemote(@RequestParam String url) throws IOException {
        return urlTitleService.getTitleByUrl(url);
    }
}
