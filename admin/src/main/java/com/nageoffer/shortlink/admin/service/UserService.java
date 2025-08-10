package com.nageoffer.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nageoffer.shortlink.admin.dao.entity.UserDO;
import com.nageoffer.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.nageoffer.shortlink.admin.dto.req.UserUpdateReqDTO;
import com.nageoffer.shortlink.admin.dto.resp.UserRespDTO;

/**
 * @author 20784
 * 用户接口层
 */
public interface UserService extends IService<UserDO> {

    /**
     * 根据用户名查询用户信息
     * @param username 用户名
     * @return 用户返回实体
     */
    UserRespDTO getUserByUsername(String username);

    /**
     * 查询用户名是否已存在
     * @param username 用户名
     * @return True-已存在,False-不存在,可以正常使用
     */
    Boolean hasUsername(String username);

    /**
     * 注册用户
     * @param requestParam 用户注册请求参数
     */
    void register(UserRegisterReqDTO requestParam);

    /**
     * 更新用户信息
     * @param requestParam 用户信息更新请求参数
     */
    void update(UserUpdateReqDTO requestParam);
}
