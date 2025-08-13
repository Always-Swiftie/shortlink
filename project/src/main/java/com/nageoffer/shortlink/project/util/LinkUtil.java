package com.nageoffer.shortlink.project.util;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.nageoffer.shortlink.project.common.constance.ShortLinkConstant;

import java.util.Date;
import java.util.Optional;

/**
 * 短链接工具类
 * @author 20784
 */
public class LinkUtil {

    /**
     * 获取短链接缓存有效时间
     * @param validDate 过期时间
     * @return 缓存有效时间长度
     */
    public static long getLinkCacheValidDate(Date validDate){
        return Optional.ofNullable(validDate)
                .map(each -> DateUtil.between(new Date(),each, DateUnit.MS))
                .orElse(ShortLinkConstant.DEFAULT_CACHE_EXPIRE_TIME);
    }

}
