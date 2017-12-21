package com.yihu.ehr.resolve.job;

import com.yihu.ehr.hbase.HBaseAdmin;
import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.resolve.util.PackResolveLogger;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Task - 定时检查集群状态，提高解析任务容错率
 * Created by progr1mmer on 2017/12/15.
 */
@Component
public class HealthCheckTask {

    private static String  SVR_REDIS = "svr-redis";
    private static String  SVR_EHR_BASIC = "svr-ehr-basic";
    private static String  SVR_PACK_MGR = "svr-pack-mgr";

    @Value("${resolve.job.init-size}")
    private int jobInitSize;
    @Value("${resolve.job.cron-exp}")
    private String jobCronExp;
    @Autowired
    private Scheduler scheduler;
    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    private HBaseAdmin hBaseAdmin;

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

    //@Scheduled(cron = "0/4 * * * * ?")
    @Scheduled(cron = "0 0 0/1 * * ?")
    public void startTask() {
        PackResolveLogger.info("Health Check:" + new Date());
        GroupMatcher groupMatcher = GroupMatcher.groupEquals("PackResolve");
        //检查集群信息
        try {
            hBaseAdmin.isTableExists("HealthProfile");
        }catch (Exception e) {
            PackResolveLogger.error(e.getMessage());
            try {
                Set<JobKey> jobKeySet = scheduler.getJobKeys(groupMatcher);
                if(jobKeySet != null) {
                    for (JobKey jobKey : jobKeySet) {
                        scheduler.deleteJob(jobKey);
                    }
                }
            }catch (SchedulerException se) {
                PackResolveLogger.error(se.getMessage());
            }
            return;
        }
        //检查微服务信息
        List<ServiceInstance> redis = discoveryClient.getInstances(SVR_REDIS);
        List<ServiceInstance> basic = discoveryClient.getInstances(SVR_EHR_BASIC);
        List<ServiceInstance> mgr = discoveryClient.getInstances(SVR_PACK_MGR);
        if(redis.isEmpty() || basic.isEmpty() || mgr.isEmpty()) {
            try {
                Set<JobKey> jobKeySet = scheduler.getJobKeys(groupMatcher);
                if(jobKeySet != null) {
                    for (JobKey jobKey : jobKeySet) {
                        scheduler.deleteJob(jobKey);
                    }
                }
            }catch (SchedulerException e) {
                PackResolveLogger.error(e.getMessage());
            }
            return;
        }
        try {
            Set<JobKey> jobKeySet = scheduler.getJobKeys(groupMatcher);
            if(jobKeySet != null) {
                int activeCount = jobKeySet.size();
                for (int i = 0; i < jobInitSize - activeCount; i++) {
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
            }else {
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
            }
        }catch (Exception e) {
            PackResolveLogger.error(e.getMessage());
        }
    }
}
