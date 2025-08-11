package com.nageoffer.shortlink.admin.dto.req;

import lombok.Data;

/**
 * 短链接分组排序请求参数
 * @author 20784
 */
@Data
public class ShortLinkGroupSortReqDTO {

    /**
     * 分组id
     */
    private String gid;

    /**
     * 顺序
     */
    private Integer sortOrder;
}
