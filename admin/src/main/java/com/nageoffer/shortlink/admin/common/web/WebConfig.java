package com.nageoffer.shortlink.admin.common.web;

import com.nageoffer.shortlink.admin.common.biz.user.UserFlowRiskControlInterceptor;
import com.nageoffer.shortlink.admin.common.biz.user.UserTransmitInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final UserTransmitInterceptor userTransmitInterceptor;
    private final UserFlowRiskControlInterceptor userFlowRiskControlInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userTransmitInterceptor)
                .addPathPatterns("/**"); // 默认全局拦截
        registry.addInterceptor(userFlowRiskControlInterceptor)
                .addPathPatterns("/**");
    }
}
