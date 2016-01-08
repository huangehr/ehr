package com.yihu.ehr;

import com.yihu.ehr.std.ThriftServiceServerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableDiscoveryClient
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class StandardServiceApp {

    public static void main(String[] args) {
        SpringApplication.run(StandardServiceApp.class, args);
    }

    @Bean
    ThriftServiceServerFactory thriftServiceServerFactory(){
        ThriftServiceServerFactory thriftServiceServerFactory = new ThriftServiceServerFactory();
        return thriftServiceServerFactory;
    }
}
