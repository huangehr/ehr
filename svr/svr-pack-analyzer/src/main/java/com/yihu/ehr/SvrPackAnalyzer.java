package com.yihu.ehr;

import com.yihu.ehr.analyze.config.SchedulerConfig;
import com.yihu.ehr.analyze.service.scheduler.SchedulerService;
import com.yihu.ehr.analyze.service.scheduler.WarningSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableAutoConfiguration(exclude = {
        SecurityAutoConfiguration.class})
@ComponentScan
@EnableDiscoveryClient
@EnableFeignClients
@EnableScheduling
@EnableAsync
@EnableSpringDataWebSupport
public class SvrPackAnalyzer extends SpringBootServletInitializer implements CommandLineRunner {

    @Autowired
    private SchedulerService schedulerService;
    @Autowired
    private SchedulerConfig schedulerConfig;
    @Autowired
    private WarningSchedulerService warningSchedulerService;

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        SpringApplication.run(SvrPackAnalyzer.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
      schedulerService.addJob(schedulerConfig.getJobMinSize(), schedulerConfig.getCronExp());
//        schedulerService.addJob(schedulerConfig.getJobMinSize(), schedulerConfig.getCronExp());
        warningSchedulerService.init();
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SvrPackAnalyzer.class);
    }
}