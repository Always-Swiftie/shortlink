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
     * 排序
     */
    private Integer sortOrder;

    /**
     * 分组下短链接数量
     */
    private Integer shortLinkCount;
}
