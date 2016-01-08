package com.yihu.ehr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class PackageServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(PackageServiceApp.class, args);
    }

    @Bean
    ThriftServiceServerFactory thriftServiceServerFactory(){
        ThriftServiceServerFactory thriftServiceServerFactory = new ThriftServiceServerFactory();
        return thriftServiceServerFactory;
    }
}
