package com.nageoffer.shortlink.admin.dto.resp;


import lombok.Data;

/**
 * 短链接分组持久层
 * @author 20784
 */
@Data
public class ShortLinkGroupRespDTO {

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 分组名称
     */
    private String name;

    /**
     * 创建分组用户名
     */
    private String username;

    /**
     * 排序
     */
    private Integer sortOrder;
}
