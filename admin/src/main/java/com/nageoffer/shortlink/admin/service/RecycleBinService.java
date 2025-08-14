package com.nageoffer.shortlink.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nageoffer.shortlink.admin.dto.req.RecycleBinPageReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;

/**
 * 短链接回收站 -- 后管
 * @author 20784
 */
public interface RecycleBinService {

    IPage<ShortLinkPageRespDTO> pageRecycleBin(RecycleBinPageReqDTO requestParam);
}
