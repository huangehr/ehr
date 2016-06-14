package com.yihu.ehr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.jmx.support.RegistrationPolicy;

@EnableFeignClients
@SpringBootApplication
@EnableDiscoveryClient
public class AppMgrSvrApp extends SpringBootServletInitializer{

	public static void main(String[] args) {
		SpringApplication.run(AppMgrSvrApp.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(AppMgrSvrApp.class);
	}
}
