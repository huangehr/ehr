package com.yihu.ehr.event.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.util.Assert;

/**
 * Created by progr1mmer on 2018/07/13.
 */
@ConfigurationProperties(prefix = "event.job")
@Configuration
public class SchedulerConfig {

    private int initSize;
    private int maxSize;
    private String cronExp;

    public int getInitSize() {
        return initSize;
    }

    public void setInitSize(int initSize) {
        this.initSize = initSize;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public String getCronExp() {
        return cronExp;
    }

    public void setCronExp(String cronExp) {
        this.cronExp = cronExp;
    }

    @Bean
    SchedulerFactoryBean schedulerFactoryBean(){
        Assert.notNull(cronExp, "Can not found event job config");
        SchedulerFactoryBean bean = new SchedulerFactoryBean();
        bean.setAutoStartup(true);
        bean.setSchedulerName("EventProcessScheduler");
        return bean;
    }

}
