package com.yihu.ehr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableSolrRepositories(basePackages = {"com.yihu.ehr.profile"}, multicoreSupport = true)
public class HealthProfileSearchSvrApp {
    public static void main(String[] args) {
        SpringApplication.run(HealthProfileSearchSvrApp.class, args);
    }
}
