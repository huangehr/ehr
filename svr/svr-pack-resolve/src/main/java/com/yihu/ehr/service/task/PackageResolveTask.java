package com.yihu.ehr.service.task;

import com.yihu.ehr.exception.ApiException;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    private int count = 0;
    private String cronExp = "0/2 * * * * ?";

    @Autowired
    private Scheduler scheduler;

    @Scheduled(cron = "0 0 3 * * ?")
    public void startTask() {
        try {
            for (int i = 0; i < total; ++i) {
                String suffix = UUID.randomUUID().toString().substring(0, 8);
                JobDetail jobDetail = newJob(PackageResourceJob.class)
                        .withIdentity("PackResolveJob-" + suffix)
                        .build();
                CronTrigger trigger = newTrigger()
                        .withIdentity("PackResolveTrigger-" + suffix)
                        .withSchedule(CronScheduleBuilder.cronSchedule(cronExp))
                        .startNow()
                        .build();
                scheduler.scheduleJob(jobDetail, trigger);
                count = i + 1;
            }
        } catch (Exception e) {
            //删除任务
            try {
                Set<JobKey> jobKeySet = scheduler.getJobKeys(null);
                for (JobKey jobKey : jobKeySet) {
                    scheduler.deleteJob(jobKey);
                    if (--count == 0) break;
                }
            } catch (SchedulerException se) {
                throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, se.getMessage());
            }
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } finally {
            count = 0;
        }
    }

}
