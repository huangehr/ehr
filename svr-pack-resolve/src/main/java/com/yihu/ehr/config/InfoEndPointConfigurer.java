package com.yihu.ehr.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/*@RestController
public class MyWebMvcConfig {
    @RequestMapping(value = "/info")
    void handleFoo(HttpServletResponse response) throws IOException {
        response.sendRedirect("/swagger-ui.html");
    }
}*/

@Configuration
public class InfoEndPointConfigurer extends WebMvcConfigurerAdapter {
    @Autowired
    HandlerInterceptor interceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //registry.addInterceptor(getYourInterceptor());
    }

    public static class InfoInterceptor{
    }
}
