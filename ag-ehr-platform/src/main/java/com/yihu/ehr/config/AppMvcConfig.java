package com.yihu.ehr.config;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.interceptor.RateLimitInterceptor;
import com.yihu.ehr.interceptor.UserAgentInterceptor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
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
@ComponentScan("com.yihu.ehr.api")
public class AppMvcConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RateLimitInterceptor()).addPathPatterns(ApiVersion.Version1_0 + "/**");
        registry.addInterceptor(new UserAgentInterceptor()).addPathPatterns(ApiVersion.Version1_0 + "/**");
    }
}
