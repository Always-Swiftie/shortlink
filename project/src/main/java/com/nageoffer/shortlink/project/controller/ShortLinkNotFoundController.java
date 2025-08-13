package com.nageoffer.shortlink.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 短链接不存在跳转控制器
 * @author 20784
 */
@Controller
public class ShortLinkNotFoundController {

    /**
     * 跳转短链接不存在页面
     * @return
     */
    @RequestMapping("/page/notfound")
    public String notFoundPage(){
        return "notfound";
    }
}
