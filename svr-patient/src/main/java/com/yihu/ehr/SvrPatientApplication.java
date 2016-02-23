package com.yihu.ehr;

import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class SvrPatientApplication /*implements ApplicationContextAware*/{

	public static void main(String[] args) {
		SpringApplication.run(SvrPatientApplication.class, args);
	}

	/*@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		//force the bean to get loaded as soon as possible
		applicationContext.getBean("requestMappingHandlerAdapter");
	}*/
}
