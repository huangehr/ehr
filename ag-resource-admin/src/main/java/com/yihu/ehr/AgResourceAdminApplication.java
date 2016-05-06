package com.yihu.ehr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.integration.jmx.config.EnableIntegrationMBeanExport;
import org.springframework.jmx.support.RegistrationPolicy;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableDiscoveryClient
@RestController
@EnableFeignClients
@EnableZuulProxy
@EnableIntegrationMBeanExport(registration = RegistrationPolicy.REPLACE_EXISTING)
public class AgResourceAdminApplication extends SpringBootServletInitializer{

	public static void main(String[] args) {
		SpringApplication.run(AgResourceAdminApplication.class, args);
	}

	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder){
		return builder.sources(AgResourceAdminApplication.class);
	}
}
