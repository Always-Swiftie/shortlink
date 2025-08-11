package com.nageoffer.shortlink.admin.dto.req;

import lombok.Data;

/**
 * 短链接分组修改参数
 * @author 20784
 */
@Data
public class ShortLinkGroupUpdateReqDTO
{
    /**
     * 分组标识
     */
    private String gid;

    /**
     * 分组名
     */
    private String name;
}
