package com.yihu.ehr;

//import com.yihu.ehr.thrift.ThriftServer;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class PackageServiceApp  implements ApplicationContextAware {
    public static void main(String[] args) {
        SpringApplication.run(PackageServiceApp.class, args);
    }

//    @PostConstruct
//    public void thriftServer(){
//        try {
//            ThriftServer thriftServer = new ThriftServer();
//            thriftServer.setPort(6011);
//
//            List<Object> services = new ArrayList();
//            services.add(new PackageService());
//
//            thriftServer.startServer(services, ThriftServer.ServerMode.BlockingMode);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        //force the bean to get loaded as soon as possible
        applicationContext.getBean("requestMappingHandlerAdapter");
    }
}
