package com.yihu.quota;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

/**
 * Created by janseny on 2017/06/16.
 */
@Configuration
@SpringBootApplication
@EnableAutoConfiguration(exclude = {
        SecurityAutoConfiguration.class,
        ManagementWebSecurityAutoConfiguration.class})
@ComponentScan(basePackages = { "com.yihu" })
@EnableFeignClients
@EnableDiscoveryClient //服务注册到发现服务
@EnableEurekaClient
@EnableSpringDataWebSupport
@EnableJpaRepositories(basePackages="com.yihu.quota.dao.jpa")
public class SvrQuotaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SvrQuotaApplication.class, args);
    }
}