package com.yihu.ehr.configuration;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * 配置服务。
 *
 * 关于Spring Cloud Config Server入门，参见：http://projects.spring.io/spring-cloud/spring-cloud.html#_spring_cloud_config
 */
@SpringBootApplication
@EnableConfigServer
@EnableDiscoveryClient
public class ConfigurationServiceApp {
	public static void main(String[] args) {
        new SpringApplicationBuilder(ConfigurationServiceApp.class)
                .web(true)
                .run(args);
	}
}
