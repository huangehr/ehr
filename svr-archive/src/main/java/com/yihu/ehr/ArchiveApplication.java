package com.yihu.ehr;

import com.yihu.ehr.lang.ServiceRegister;
import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.TProcessor;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class ArchiveApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplicationBuilder(ArchiveApplication.class)
                .web(true)
                .application();
//
//        springApplication.addListeners(new ApplicationListener<ApplicationEvent>() {
//            @Override
//            public void onApplicationEvent(ApplicationEvent event) {
//                if (event instanceof ApplicationReadyEvent) {
//                    try {
//                        ServiceRegister.registerServices();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });

        SpringApplication.run(ArchiveApplication.class, args);
    }
}

@Component
class MyListener implements ApplicationListener<ApplicationEvent> {

    @Value("${server.port}")
    int listenPort;

    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationReadyEvent) {
            try {
                //TMultiplexedProcessor processor = ServiceRegister.registerServices();

                //TServerTransport serverTransport = new TServerSocket(listenPort);
                //TServer.AbstractServerArgs abstractServerArgs = new TServer.AbstractServerArgs(serverTransport);
                //TSimpleServer simpleServer = new TSimpleServer(processor, serverTransport);
                //simpleServer.serve();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}