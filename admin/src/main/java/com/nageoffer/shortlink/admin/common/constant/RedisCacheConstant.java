package com.nageoffer.shortlink.admin.common.constant;

/**
 * 后管 Redis 缓存常量类
 * @author 20784
 */
public class RedisCacheConstant {

    /**
     * 用户注册分布式锁
     */
    public static final String LOCK_USER_REGISTER_KEY = "shortlink:lock_user_register:";

    /**
     * 分组创建分布式锁
     */
    public static final String LOCK_GROUP_CREATE_KEY = "shortlink:lock_group_create:";
}
