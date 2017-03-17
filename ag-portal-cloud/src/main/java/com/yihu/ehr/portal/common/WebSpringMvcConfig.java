package com.yihu.ehr.portal.common;
import com.yihu.ehr.portal.common.GlobalHandlerExceptionResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
/**
 * Created by hzp on 20170317.
 */
@Component
public class WebSpringMvcConfig extends WebMvcConfigurerAdapter {

    @Bean
    public GlobalHandlerExceptionResolver globalHandlerExceptionResolver() {
        return new GlobalHandlerExceptionResolver();
    }
}