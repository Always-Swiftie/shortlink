package com.nageoffer.shortlink.admin.remote.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

/**
 * 短链接分页请求参数
 * @author 20784
 */
@Data
public class ShortLinkPageReqDTO extends Page {

    /**
     * 分组标识(分页依据)
     */
    private String gid;

    /**
     * 排序标识
     */
    private String orderTag;
}
