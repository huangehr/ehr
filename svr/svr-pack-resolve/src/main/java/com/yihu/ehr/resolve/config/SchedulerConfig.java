package com.yihu.ehr.resolve.config;

import com.yihu.ehr.resolve.job.PackageResourceJob;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.annotation.PostConstruct;
import java.util.UUID;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

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

    @Value("${resolve.job.init-size}")
    private int jobInitSize;
    @Value("${resolve.job.cron-exp}")
    private String jobCronExp;
    @Autowired
    private Scheduler scheduler;

    @PostConstruct
    private void init() {
        try {
            for (int i = 0; i < jobInitSize; i++) {
                String suffix = UUID.randomUUID().toString().substring(0, 8);
                JobDetail jobDetail = newJob(PackageResourceJob.class)
                        .withIdentity("PackResolveJob-" + suffix, "PackResolve")
                        .build();
                CronTrigger trigger = newTrigger()
                        .withIdentity("PackResolveTrigger-" + suffix, "PackResolve")
                        .withSchedule(CronScheduleBuilder.cronSchedule(jobCronExp))
                        .startNow()
                        .build();
                scheduler.scheduleJob(jobDetail, trigger);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
