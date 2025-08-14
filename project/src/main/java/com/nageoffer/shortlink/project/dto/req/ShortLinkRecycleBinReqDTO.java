package com.nageoffer.shortlink.project.dto.req;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.util.List;

/**
 * 回收站短链接分页查询请求参数
 * @author 20784
 */
@Data
public class ShortLinkRecycleBinReqDTO extends Page {

    /**
     * 当前用户分组标识集合
     */
    private List<String> gidList;
}
