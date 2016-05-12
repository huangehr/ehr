package com.yihu.ehr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SvrPictureApplication {

	public static void main(String[] args) {
		SpringApplication.run(SvrPictureApplication.class, args);
	}
}
