package com.yihu.ehr.config;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.interceptor.RateLimitInterceptor;
import com.yihu.ehr.interceptor.UserAgentInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * MVC配置器。注册以下截取器：
 * - 请求频率截取器
 *
 * @author Sand
 * @version 1.0
 * @created 2016.02.26 16:04
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private RateLimitInterceptor rateLimitInterceptor;

    @Autowired
    private UserAgentInterceptor userAgentInterceptor;

    /**
     * 注册截取器。
     *
     * @param registry
     */
    /*@Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userAgentInterceptor).addPathPatterns(ApiVersion.Version1_0 + "/**");
        registry.addInterceptor(rateLimitInterceptor).addPathPatterns("/**").excludePathPatterns("/swagger**");
    }*/

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
