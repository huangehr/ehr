package com.yihu.ehr.resolve.job;

import com.yihu.ehr.resolve.config.SchedulerConfig;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Set;
import java.util.UUID;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Created by progr1mmer on 2018/7/13.
 */
@Component
public class SchedulerManager {

    private int jobSetSize;
    @Autowired
    private Scheduler scheduler;
    @Autowired
    private SchedulerConfig schedulerConfig;

    @PostConstruct
    private void init() throws Exception {
        try {
            for (int i = 0; i < schedulerConfig.getInitSize(); i++) {
                String suffix = UUID.randomUUID().toString().substring(0, 8);
                JobDetail jobDetail = newJob(PackageResolveJob.class)
                        .withIdentity("PackResolveJob-" + suffix, "PackResolve")
                        .build();
                CronTrigger trigger = newTrigger()
                        .withIdentity("PackResolveJob-" + suffix, "PackResolve")
                        .withSchedule(CronScheduleBuilder.cronSchedule(schedulerConfig.getCronExp()))
                        .startNow()
                        .build();
                scheduler.scheduleJob(jobDetail, trigger);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.jobSetSize = schedulerConfig.getInitSize();
    }

    public int getJobSetSize() {
        return jobSetSize;
    }

    public int getJobSize() throws Exception {
        GroupMatcher groupMatcher = GroupMatcher.groupEquals("PackResolve");
        Set<JobKey> jobKeys = scheduler.getJobKeys(groupMatcher);
        return jobKeys.size();
    }

    public void addJob (int count, String cronExp) throws Exception {
        int addCount = 0;
        GroupMatcher groupMatcher = GroupMatcher.groupEquals("PackResolve");
        Set<JobKey> jobKeys = scheduler.getJobKeys(groupMatcher);
        int activeJob = jobKeys.size();
        for (int i = 0; i < count; i++) {
            if (i + activeJob >= schedulerConfig.getMaxSize()) {
                break;
            }
            String suffix = UUID.randomUUID().toString().substring(0, 8);
            JobDetail jobDetail = newJob(PackageResolveJob.class)
                    .withIdentity("PackResolveJob-" + suffix, "PackResolve")
                    .build();
            CronTrigger trigger = newTrigger()
                    .withIdentity("PackResolveJob-" + suffix, "PackResolve")
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExp))
                    .startNow()
                    .build();
            scheduler.scheduleJob(jobDetail, trigger);
            addCount = i + 1;
        }
        this.jobSetSize += addCount;
        if (this.jobSetSize > schedulerConfig.getMaxSize()) {
            jobSetSize = schedulerConfig.getMaxSize();
        }
    }

    public void minusJob (int count) throws Exception {
        int minusCount = count;
        GroupMatcher groupMatcher = GroupMatcher.groupEquals("PackResolve");
        Set<JobKey> jobKeySet = scheduler.getJobKeys(groupMatcher);
        for (JobKey jobKey : jobKeySet) {
            scheduler.deleteJob(jobKey);
            if (--count == 0) {
                break;
            }
        }
        this.jobSetSize -= minusCount;
        if (this.jobSetSize < 0) {
            jobSetSize = 0;
        }
    }

}
