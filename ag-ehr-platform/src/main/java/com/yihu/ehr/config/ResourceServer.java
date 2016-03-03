package com.yihu.ehr.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.03 20:50
 */
@Configuration
@EnableResourceServer // [2]
public class ResourceServer extends ResourceServerConfigurerAdapter {
    private static final String RESOURCE_ID = "blog_resource";

    @Override // [3]
    public void configure(HttpSecurity http) throws Exception {
        http
                .requestMatchers().antMatchers("/","/admin/beans").and()
                .authorizeRequests()
                .anyRequest().access("#oauth2.hasScope('read')"); //[4]
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId(RESOURCE_ID);
    }

}