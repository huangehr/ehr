package com.yihu.ehr.zuul.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by progr1mmer on 2018/8/24.
 */
@Configuration
public class ComplexRouteConfig {

    @Value("${zuul.config-priority:false}")
    private Boolean configPriority;

    @Autowired
    private ZuulProperties zuulProperties;
    @Autowired
    private ServerProperties server;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Bean
    public JdbcZuulRouteService jdbcZuulRouteService(JdbcTemplate jdbcTemplate){
        return new JdbcZuulRouteService(jdbcTemplate);
    }

    @Bean
    public ComplexRouteLocator routeLocator() {
        return new ComplexRouteLocator(
                server.getServletPrefix(),
                zuulProperties,
                jdbcZuulRouteService(jdbcTemplate),
                configPriority);
    }

}
