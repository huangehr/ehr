package com.yihu.ehr.resources;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableEurekaClient
@ComponentScan("com.yihu.ehr")
public class ResourcesServiceApp {

    public static void main(String[] args) {
        SpringApplication.run(ResourcesServiceApp.class, args);
    }
}
