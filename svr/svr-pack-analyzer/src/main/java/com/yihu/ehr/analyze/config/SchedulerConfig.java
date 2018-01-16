package com.yihu.ehr.analyze.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import static org.quartz.JobBuilder.newJob;

/**
 * @author Airhead
 * @version 1.0
 * @created 2018.01.16
 */
@Configuration
public class SchedulerConfig {

    @Bean
    SchedulerFactoryBean schedulerFactoryBean(){
        SchedulerFactoryBean bean = new SchedulerFactoryBean();
        bean.setAutoStartup(true);
        bean.setSchedulerName("PackageAnalyzerScheduler");
        return bean;
    }

}
