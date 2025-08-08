package com.nageoffer.shortlink.admin.dao.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * @author 20784
 */
@Data
public class UserDO {

    /** ID */
    private Long id;

    /** 用户名 */
    private String username;

    /** 密码 */
    private String password;

    /** 真实姓名 */
    private String realName;

    /** 手机号 */
    private String phone;

    /** 邮箱 */
    private String mail;

    /** 注销时间戳 */
    private Long deletionTime;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 修改时间 */
    private LocalDateTime updateTime;

    /** 删除标识 0：未删除 1：已删除 */
    private Integer delFlag;
}

