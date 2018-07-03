package com.yihu.ehr.analyze.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * @author Airhead
 * @version 1.0
 * @created 2018.01.16
 */
@Configuration
public class SchedulerConfig {
    @Value("${ehr.job.maxSize}")
    private int jobMaxSize;
    @Value("${ehr.job.minSize}")
    private int jobMinSize;
    @Value("${ehr.job.cronExp}")
    private String cronExp;

    public int getJobMaxSize() {
        return jobMaxSize;
    }

    public int getJobMinSize() {
        return jobMinSize;
    }

    public String getCronExp() {
        return cronExp;
    }

    @Bean
    SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean bean = new SchedulerFactoryBean();
        bean.setAutoStartup(true);
        bean.setSchedulerName("PackageAnalyzerScheduler");
        return bean;
    }

}
