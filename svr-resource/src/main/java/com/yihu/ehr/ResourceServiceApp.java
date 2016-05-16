package com.yihu.ehr;

import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ResourceServiceApp {

    public static void main(String[] args) {
        SpringApplication.run(ResourceServiceApp.class, args);
    }

}
