package com.yihu.ehr;

import com.yihu.ehr.pack.thrift.PackageService;
import com.yihu.ehr.thrift.ThriftServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableDiscoveryClient
public class PackageServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(PackageServiceApp.class, args);
    }

    @PostConstruct
    public void thriftServer(){
        try {
            ThriftServer thriftServer = new ThriftServer();
            thriftServer.setPort(6011);

            List<Object> services = new ArrayList();
            services.add(new PackageService());

            thriftServer.startServer(services, ThriftServer.ServerMode.BlockingMode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
