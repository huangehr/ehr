package com.yihu.ehr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableSpringDataWebSupport
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class ResourceSvrApp extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(ResourceSvrApp.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ResourceSvrApp.class);
    }
}
