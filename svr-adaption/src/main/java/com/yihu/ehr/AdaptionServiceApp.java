package com.yihu.ehr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
@EnableDiscoveryClient
@RestController
@EnableFeignClients
//@EnableAspectJAutoProxy(proxyTargetClass = true)
public class AdaptionServiceApp {


    public static void main(String[] args) {
        SpringApplication.run(AdaptionServiceApp.class, args);
    }
}
