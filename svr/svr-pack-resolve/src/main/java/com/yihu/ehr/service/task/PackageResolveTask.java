package com.yihu.ehr.service.task;

import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Created by progr1mmer on 2017/10/10.
 */
@Component
public class PackageResolveTask {

    private int total = 8;
    private String cronExp = "0/2 * * * * ?";

    @Autowired
    private Scheduler scheduler;

    @Scheduled(cron = "0 0/1 * * * ?")
    public void startTask() {
        try {
            GroupMatcher groupMatcher = GroupMatcher.groupEquals("PackResolve");
            Set<JobKey> jobKeySet = scheduler.getJobKeys(groupMatcher);
            if(null != jobKeySet) {
                int activeCount = jobKeySet.size();
                for (int i = 0; i < total - activeCount; ++i) {
                    String suffix = UUID.randomUUID().toString().substring(0, 8);
                    JobDetail jobDetail = newJob(PackageResourceJob.class)
                            .withIdentity("PackResolveJob-" + suffix, "PackResolve")
                            .build();
                    CronTrigger trigger = newTrigger()
                            .withIdentity("PackResolveTrigger-" + suffix)
                            .withSchedule(CronScheduleBuilder.cronSchedule(cronExp))
                            .startNow()
                            .build();
                    scheduler.scheduleJob(jobDetail, trigger);
                }
            }
        } catch (Exception e) {
            //删除任务
            try {
                GroupMatcher groupMatcher = GroupMatcher.groupEquals("PackResolve");
                Set<JobKey> jobKeySet = scheduler.getJobKeys(groupMatcher);
                for (JobKey jobKey : jobKeySet) {
                    scheduler.deleteJob(jobKey);
                }
            } catch (SchedulerException se) {
                se.printStackTrace();
            }
            e.printStackTrace();
        }
    }

}
