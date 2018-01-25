package com.yihu.ehr;

import com.yihu.ehr.analyze.config.SchedulerConfig;
import com.yihu.ehr.analyze.service.scheduler.SchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.MetricExportAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAutoConfiguration(exclude = {
        SecurityAutoConfiguration.class,
        ManagementWebSecurityAutoConfiguration.class,
        MetricExportAutoConfiguration.class})
@ComponentScan
@EnableDiscoveryClient
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableFeignClients
@EnableScheduling
@EnableAsync
public class SvrPackAnalyzer extends SpringBootServletInitializer implements CommandLineRunner {

    @Autowired
    private SchedulerService schedulerService;
    @Autowired
    private SchedulerConfig schedulerConfig;

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        SpringApplication.run(SvrPackAnalyzer.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        schedulerService.addJob(schedulerConfig.getJobMinSize(), schedulerConfig.getCronExp());
    }

    @Bean
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("qc-");
        executor.initialize();
        return executor;
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SvrPackAnalyzer.class);
    }
}