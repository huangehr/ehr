package com.yihu.ehr.oauth2.config;

import com.yihu.ehr.oauth2.oauth2.EhrUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.03 21:57
 */
@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true, proxyTargetClass = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Order(1)
    @Configuration
    public static class AuthorizationApiSecurityConfig extends WebSecurityConfigurerAdapter {

        @Autowired
        private EhrUserDetailsService userDetailsService;
        @Autowired
        private PasswordEncoder passwordEncoder;
        @Override
        protected void configure(AuthenticationManagerBuilder authBuilder) throws Exception {
            authBuilder.userDetailsService(userDetailsService).passwordEncoder(new Md5PasswordEncoder());
        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring()
                    //swagger-ui界面---start
                    .antMatchers("/swagger-ui.html")
                    .antMatchers("/swagger-resources/**")
                    .antMatchers("/v2/api-docs/**")
                    .antMatchers("/configuration/**")
                    .antMatchers("/webjars/springfox-swagger-ui/**")
                    //swagger-ui界面---end
                    .antMatchers("/oauth/accessToken")
                    .antMatchers("/oauth/refreshToken")
                    .antMatchers("/oauth/validToken")
                    .antMatchers("/oauth/confirm_access")
                    .antMatchers("/oauth/sso")
                    .antMatchers("/oauth/authorize");
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().disable().requestMatcher(
                    new OrRequestMatcher(
                            new AntPathRequestMatcher("/api/v*/authorizations/**"),
                            new AntPathRequestMatcher("/api/v*/applications/*/tokens/*")
                    ))
                    .authorizeRequests()
                    .antMatchers("/api/v*/authorizations/**").hasAnyRole("USER", "CLIENT")
                    .antMatchers("/api/v*/applications/*/tokens/*").hasAnyRole("USER", "CLIENT")
                    .and()
                    .httpBasic();
        }

        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        }
    }

    //==========密码加密方式====================
    @Bean
    public PasswordEncoder passwordEncoder() {
        PasswordEncoder passwordEncoder = new Md5PasswordEncoder();
        return passwordEncoder;
    }

    @Order(2)
    @Configuration
    public static class FormSecurityConfig extends WebSecurityConfigurerAdapter {
        @Autowired
        private EhrUserDetailsService userDetailsService;

        @Override
        protected void configure(AuthenticationManagerBuilder authBuilder) throws Exception {
            authBuilder.userDetailsService(userDetailsService).passwordEncoder(new Md5PasswordEncoder());
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .csrf().disable()
                    .authorizeRequests().antMatchers("/rest/v1.0/**").permitAll()
                    .and()
                    .authorizeRequests().anyRequest().authenticated()
                    .and()
                    .formLogin().loginPage("/login").defaultSuccessUrl("/login?logout").permitAll()
                    .and().httpBasic();
        }

    }

//    /**
//     * 临时使用的配置，启用全部URI匿名访问。
//     */
//    @Order(1)
//    @Configuration
//    public static class TempSecurityConfig extends WebSecurityConfigurerAdapter {
//        @Autowired
//        private EhrUserDetailsService userDetailsService;
//
//        @Override
//        protected void configure(AuthenticationManagerBuilder authBuilder) throws Exception {
//            authBuilder.userDetailsService(userDetailsService).passwordEncoder(new Md5PasswordEncoder());
//        }
//
//        @Override
//        protected void configure(HttpSecurity http) throws Exception {
//            http.csrf().disable().antMatcher("/**").authorizeRequests().anyRequest().permitAll();
//        }
//    }
}
