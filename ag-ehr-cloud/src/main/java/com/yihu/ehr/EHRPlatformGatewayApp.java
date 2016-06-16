package com.yihu.ehr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.SocketException;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableEurekaClient
@EnableZuulProxy
@EnableScheduling
public class EHRPlatformGatewayApp extends SpringBootServletInitializer {

    public static void main(String[] args) throws SocketException {
		SpringApplication.run(EHRPlatformGatewayApp.class, args);
	}
}
