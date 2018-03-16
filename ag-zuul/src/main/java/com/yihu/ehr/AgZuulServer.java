package com.yihu.ehr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * Created by progr1mmer on 2017/12/27.
 */
@EnableEurekaClient
@EnableZuulProxy
@SpringBootApplication
public class AgZuulServer extends SpringBootServletInitializer {

    public static void main(String [] args) {
        SpringApplication.run(AgZuulServer.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(AgZuulServer.class);
    }
}
