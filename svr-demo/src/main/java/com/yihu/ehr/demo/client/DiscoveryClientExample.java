package com.yihu.ehr.demo.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.11.16 18:20
 */
//@Component
public class DiscoveryClientExample implements CommandLineRunner {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Override
    public void run(String... strings) throws Exception {
        discoveryClient.getInstances("CONFIGURATION.SERVICE").forEach((ServiceInstance s) -> {
            System.out.println("Service Id: " + s.getServiceId());
            System.out.println("Host: " + s.getHost());
            System.out.println("Port: " + s.getPort());
            System.out.println("Is secure: " + s.isSecure());
            System.out.println("URI: " + s.getUri());
        });
    }
}