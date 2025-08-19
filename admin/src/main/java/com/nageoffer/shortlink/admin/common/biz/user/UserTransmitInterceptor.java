package com.nageoffer.shortlink.admin.common.biz.user;


import cn.hutool.core.text.AntPathMatcher;
import com.alibaba.fastjson2.JSONObject;
import com.nageoffer.shortlink.admin.common.convention.exception.ClientException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import java.util.List;
import java.util.Objects;

import static com.nageoffer.shortlink.admin.common.enums.UserErrorCodeEnum.USER_TOKEN_FAIL;


/**
 * 用户信息传输过滤器
 * @author 20784
 */
@Component
@RequiredArgsConstructor
public class UserTransmitInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate stringRedisTemplate;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    // 忽略校验的路径
    private static final List<String> IGNORE_URI = List.of(
            "/api/short-link/admin/v1/user/login",
            "/api/short-link/admin/v1/user/has-username",
            "/api/short-link/admin/v1/user" // POST 创建用户的场景
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        // 忽略的 URI
        if (shouldIgnore(requestURI, method) || requestURI.equals("/api/short-link/admin/v1/user/login")) {
            return true;
        }

        String username = request.getHeader("username");
        String token = request.getHeader("token");
        if (username == null || token == null || username.isBlank() || token.isBlank()) {
            throw new ClientException(USER_TOKEN_FAIL);
        }

        String userLoginKey = "login_" + username;
        Object userInfoJsonStr = stringRedisTemplate.opsForHash().get(userLoginKey, token);
        if (Objects.isNull(userInfoJsonStr)) {
            throw new ClientException(USER_TOKEN_FAIL);
        }

        UserInfoDTO userInfoDTO = JSONObject.parseObject((String) userInfoJsonStr, UserInfoDTO.class);
        UserContext.setUser(userInfoDTO);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 清理 ThreadLocal
        UserContext.removeUser();
    }

    private boolean shouldIgnore(String uri, String method) {
        return IGNORE_URI.stream().anyMatch(ignorePath ->
                pathMatcher.match(ignorePath, uri) &&
                        !(ignorePath.equals("/api/short-link/admin/v1/user") && !"POST".equalsIgnoreCase(method))
        );
    }
}

