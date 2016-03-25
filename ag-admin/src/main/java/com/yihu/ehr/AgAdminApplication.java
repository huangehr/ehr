package com.yihu.ehr;

import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Created by AndyCai on 2016/1/19.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class AgAdminApplication  {
    public static void main(String[] args) {
        SpringApplication.run(AgAdminApplication.class, args);
    }

}
