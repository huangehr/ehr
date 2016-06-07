package com.yihu.ehr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.integration.jmx.config.EnableIntegrationMBeanExport;
import org.springframework.jmx.support.RegistrationPolicy;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableSpringDataWebSupport
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableIntegrationMBeanExport(registration = RegistrationPolicy.REPLACE_EXISTING)
public class ResourceSvrApp {
    public static void main(String[] args) {
        SpringApplication.run(ResourceSvrApp.class, args);
    }
}
