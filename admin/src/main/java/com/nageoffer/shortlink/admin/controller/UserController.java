package com.nageoffer.shortlink.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.common.convention.result.Results;
import com.nageoffer.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.nageoffer.shortlink.admin.dto.req.UserUpdateReqDTO;
import com.nageoffer.shortlink.admin.dto.resp.UserActualRespDTO;
import com.nageoffer.shortlink.admin.dto.resp.UserRespDTO;
import com.nageoffer.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制层
 * @author 20784
 */
@RestController
@RequiredArgsConstructor
public class UserController {

    //通过构造器方式注入(lombok提供)
    private final UserService userService;

    /**
     * 根据用户名查询用户信息
     */
    @GetMapping("/api/shortlink/v1/user/{username}")
    public Result<UserRespDTO> getUserByUsername(@PathVariable("username") String username){
        return Results.success(userService.getUserByUsername(username));
    }

    /**
     * 根据用户名查询未脱敏用户信息
     */
    @GetMapping("/api/shortlink/v1/user/actual/{username}")
    public Result<UserActualRespDTO> getUserActualByUsername(@PathVariable("username") String username){
        return Results.success(BeanUtil.toBean(userService.getUserByUsername(username),UserActualRespDTO.class));
    }

    /**
     * 查询用户名是否已存在
     */
    @GetMapping("/api/shortlink/v1/user/has-username")
    public Result<Boolean> hasUsername(@RequestParam("username") String username){
        return Results.success(userService.hasUsername(username));
    }

    /**
     * 注册用户
     * @param requestParam 注册用户请求参数
     */
    @PostMapping("/api/shortlink/v1/user")
    public Result<Void> registerUser(@RequestBody UserRegisterReqDTO requestParam){
        userService.register(requestParam);
        return Results.success();
    }

    @PutMapping("/api/shortlink/v1/user")
    public Result<Void> update(@RequestBody UserUpdateReqDTO requestParam){
        userService.update(requestParam);
        return Results.success();
    }

}
