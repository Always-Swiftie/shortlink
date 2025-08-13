package com.nageoffer.shortlink.project.util;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.nageoffer.shortlink.project.common.constance.ShortLinkConstant;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    /**
     * 获取网站 favicon 图标 URL
     */
    @SneakyThrows
    public static String getFavicon(String url) {
        String finalUrl = followRedirects(url);

        // 1. 先请求 HTML 页面，解析 <link rel="icon">
        String html = fetchHtml(finalUrl);
        if (html != null) {
            String iconHref = parseFaviconFromHtml(html, finalUrl);
            if (iconHref != null) {
                return iconHref;
            }
        }

        // 2. 如果 HTML 里没有，则尝试默认路径 /favicon.ico
        try {
            URL defaultFavicon = new URL(new URL(finalUrl), "/favicon.ico");
            HttpURLConnection conn = (HttpURLConnection) defaultFavicon.openConnection();
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            conn.setRequestMethod("HEAD");
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return defaultFavicon.toString();
            }
        } catch (IOException ignored) {}

        return null;
    }

    /**
     * 跟随多次重定向，返回最终 URL
     */
    private static String followRedirects(String url) throws IOException {
        String currentUrl = url;
        int redirectCount = 0;
        while (redirectCount < 5) {
            HttpURLConnection conn = (HttpURLConnection) new URL(currentUrl).openConnection();
            conn.setInstanceFollowRedirects(false);
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            conn.setRequestMethod("HEAD");

            int code = conn.getResponseCode();
            if (code == HttpURLConnection.HTTP_MOVED_PERM || code == HttpURLConnection.HTTP_MOVED_TEMP) {
                String location = conn.getHeaderField("Location");
                if (location == null) break;
                currentUrl = new URL(new URL(currentUrl), location).toString();
                redirectCount++;
            } else {
                break;
            }
        }
        return currentUrl;
    }

    /**
     * 获取 HTML 源码
     */
    private static String fetchHtml(String url) {
        try (InputStream is = new URL(url).openStream()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 从 HTML 中解析 favicon 链接
     */
    private static String parseFaviconFromHtml(String html, String baseUrl) {
        Pattern pattern = Pattern.compile("<link[^>]+rel=[\"']?(?:shortcut )?icon[\"']?[^>]*>", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            String tag = matcher.group();
            Matcher hrefMatcher = Pattern.compile("href=[\"']?([^\"'>]+)", Pattern.CASE_INSENSITIVE).matcher(tag);
            if (hrefMatcher.find()) {
                String href = hrefMatcher.group(1);
                try {
                    return new URL(new URL(baseUrl), href).toString();
                } catch (MalformedURLException ignored) {}
            }
        }
        return null;
    }


}
