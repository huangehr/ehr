package com.yihu.ehr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableFeignClients
@EnableScheduling
@EnableIntegrationMBeanExport(registration = RegistrationPolicy.REPLACE_EXISTING)
public class PackResolveServiceApp{

	public static void main(String[] args) {
		SpringApplication.run(PackResolveServiceApp.class, args);
	}
}
