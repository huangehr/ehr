package com.yihu.ehr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.MetricExportAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableAutoConfiguration(exclude = {
        SecurityAutoConfiguration.class})
@ComponentScan
@EnableDiscoveryClient
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableFeignClients
@EnableScheduling
public class SvrPackResolve extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(SvrPackResolve.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SvrPackResolve.class);
    }
}