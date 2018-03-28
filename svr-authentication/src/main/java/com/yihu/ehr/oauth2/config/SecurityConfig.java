package com.yihu.ehr.oauth2.config;

import com.yihu.ehr.oauth2.oauth2.EhrUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.03 21:57
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private EhrUserDetailsService userDetailsService;
    @Autowired
    private Md5PasswordEncoder md5PasswordEncoder;

    @Order(1)
    @Configuration
    @EnableWebSecurity
    public static class AuthorizationApiSecurityConfig extends WebSecurityConfigurerAdapter {

        @Autowired
        private AuthenticationProvider authenticationProvider;

        @Override
        protected void configure(AuthenticationManagerBuilder authBuilder) throws Exception {
            authBuilder.authenticationProvider(authenticationProvider);
            //authBuilder.userDetailsService(userDetailsService).passwordEncoder(md5PasswordEncoder);
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

    }

    @Bean
    public Md5PasswordEncoder passwordEncoder() {
        Md5PasswordEncoder passwordEncoder = new Md5PasswordEncoder();
        return passwordEncoder;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(md5PasswordEncoder);
        return authenticationProvider;
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
