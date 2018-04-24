package com.yihu.ehr.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.03 21:50
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {


    /**
     * 注册截取器。
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //registry.addInterceptor(userAgentInterceptor).addPathPatterns(ApiVersion.Version1_0 + "/**");
        //registry.addInterceptor(rateLimitInterceptor).addPathPatterns("/**").excludePathPatterns("/swagger**");
    }

    /**
     * 注册视图-控制器
     *
     * @param registry
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("/login/login");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }
}
