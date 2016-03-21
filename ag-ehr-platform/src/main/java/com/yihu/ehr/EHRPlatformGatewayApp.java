package com.yihu.ehr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableEurekaClient
@EnableZuulProxy
public class EHRPlatformGatewayApp  {
    public static void main(String[] args) {
		SpringApplication.run(EHRPlatformGatewayApp.class, args);
	}
}
