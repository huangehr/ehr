package com.yihu.ehr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.02.25 14:58
 */
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class ApplicationSecurity extends WebSecurityConfigurerAdapter {
    //@Autowired
    //private RESTAuthen

    @Override
    protected void configure(AuthenticationManagerBuilder builder) throws Exception {
        builder.inMemoryAuthentication()
                .withUser("user").password("user").roles("USER")
                .and().withUser("admin").password("admin").roles("ADMIN");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/api").anonymous().anyRequest().authenticated().and().httpBasic();;
        http.authorizeRequests().antMatchers("/api/**").authenticated();
        http.csrf().disable();
        //http.exceptionHandling().authenticationEntryPoint(null);
        //http.formLogin().successHandler(null);
        //http.formLogin().failureHandler(null);
        //
    }
}
