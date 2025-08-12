package com.nageoffer.shortlink.admin.dto.resp;

import lombok.Data;

/**
 * 短链接分组查询返回参数
 * @author 20784
 */
@Data
public class ShortLinkGroupCountQueryRespDTO {

    /**
     * 分组标识 gid
     */
    private String gid;

    /**
     * 该分组下短链接数量
     */
    private Integer shortLinkCount;
}
