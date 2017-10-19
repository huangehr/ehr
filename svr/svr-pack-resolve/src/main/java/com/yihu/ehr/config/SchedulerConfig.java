package com.yihu.ehr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.31 10:30
 */
@Configuration
public class SchedulerConfig {
    @Bean
    SchedulerFactoryBean schedulerFactoryBean(){
        SchedulerFactoryBean bean = new SchedulerFactoryBean();
        bean.setAutoStartup(true);
        bean.setSchedulerName("PackageResolveScheduler");
        return bean;
    }
}
