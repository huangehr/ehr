package com.yihu.ehr.config;

import com.yihu.ehr.service.oauth2.EhrUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
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
    public static class AuthorizationApiSecurityConfig extends WebSecurityConfigurerAdapter{
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
    }

    @Order(2)
    @Configuration
    public static class FormSecurityConfig extends WebSecurityConfigurerAdapter{
        @Autowired
        private EhrUserDetailsService userDetailsService;

        @Override
        protected void configure(AuthenticationManagerBuilder authBuilder) throws Exception {
            authBuilder.userDetailsService(userDetailsService).passwordEncoder(new Md5PasswordEncoder());
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests().anyRequest().authenticated()
                    .and()
                    .formLogin().loginPage("/login").defaultSuccessUrl("/login?logout").permitAll()
                    .and()
                    .antMatcher("/rest/v1.0/token").authorizeRequests().anyRequest().permitAll()
                    .and().httpBasic();
        }
    }
}
