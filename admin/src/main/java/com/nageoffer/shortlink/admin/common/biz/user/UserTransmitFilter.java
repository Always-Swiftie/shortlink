package com.nageoffer.shortlink.admin.common.biz.user;


import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URLDecoder;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 用户信息传输过滤器
 * @author 20784
 */
@RequiredArgsConstructor
public class UserTransmitFilter implements Filter {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String requestURI = httpServletRequest.getRequestURI();
        if (!requestURI.equals("/api/shortlink/v1/user/login")) {
            String username = httpServletRequest.getHeader("username");
            String token = httpServletRequest.getHeader("token");
            String userLoginKey = "login_" + username;
            Object userInfoJsonStr = stringRedisTemplate.opsForHash().get(userLoginKey, token);
            if (userInfoJsonStr != null) {
                UserInfoDTO userInfoDTO = JSONObject.parseObject((String) userInfoJsonStr, UserInfoDTO.class);
            }
        }
        try {
            filterChain.doFilter(servletRequest,servletResponse);
        }finally {
            UserContext.removeUser();
        }
    }

}

