package com.yihu.ehr.config;


import com.yihu.ehr.constants.ApiVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * UI访问安全配置策略：
 * - 登录/文档/api根目录/css全部允许公开访问
 * - swagger调用接口只允许登录才能访问，若未登录自动重定向到/login界面
 * - 其他业务UI均需要配置
 * <p>
 * API安全配置策略参见：{@link ResourceServerConfig}
 *
 * @author Sand
 * @version 1.0
 * @created 2016.03.02 14:56
 */
//@Order(2)
//@Configuration
//@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private FakeUserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .formLogin().loginPage("/login").failureUrl("/login?error").defaultSuccessUrl("/login?logout").permitAll()
                .and()
                .authorizeRequests().antMatchers("/css/**", "/docs", "/api").permitAll()
                .and()
                .authorizeRequests().antMatchers("/swagger**", "/login/oauth/**").authenticated()
                .and()
                .logout().permitAll()
                .and()
                .httpBasic();

        List<String> oauthMatcher = new ArrayList<>(ApiVersion.class.getFields().length);
        for (Field field : ApiVersion.class.getFields()) {
            if (field.getName().startsWith("Version")) {
                oauthMatcher.add(field.get(null) + "/**");
            }
        }
    }
}