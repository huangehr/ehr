package com.yihu.ehr.service;

import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.08.27 16:03
 */
@Service
public class JobManager implements XJobManager {
    public final static String DefaultJobGroup = "EhrJobGroup";
    public final static String DefaultTriggerGroup = "EhrTriggerGroup";
    
    public void startScheduler() throws SchedulerException {
        Scheduler scheduler = getScheduler();
        if (null != scheduler && scheduler.isShutdown()) {
            scheduler.start();
        }
    }

    public void shutdownScheduler(boolean waitForJobsToComplete) throws SchedulerException {
        Scheduler scheduler = getScheduler();
        if (null != scheduler && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    public void standbyScheduler() throws SchedulerException {
        Scheduler scheduler = getScheduler();
        if (null != scheduler && !scheduler.isShutdown()) {
            scheduler.standby();
        }
    }

    public void resumeScheduler() throws SchedulerException {
        Scheduler scheduler = getScheduler();
        if (null != scheduler && !scheduler.isShutdown()) {
            scheduler.start();
        }
    }

    public boolean isRunning() throws SchedulerException {
        Scheduler scheduler = getScheduler();
        return scheduler.isStarted();
    }

    public boolean isStandBy() throws SchedulerException {
        Scheduler scheduler = getScheduler();
        return scheduler.isInStandbyMode();
    }

    public boolean isShutdown() throws SchedulerException {
        Scheduler scheduler = getScheduler();
        return scheduler.isShutdown();
    }

    public List<JobDetail> getJobList() throws SchedulerException {
        Scheduler scheduler = getScheduler();
        Set<JobKey> jobKeySet = scheduler.getJobKeys(GroupMatcher.groupEquals(DefaultJobGroup));

        List<JobDetail> jobDetails = new ArrayList<>(jobKeySet.size());
        for (JobKey jobKey : jobKeySet){
            jobDetails.add(scheduler.getJobDetail(jobKey));
        }

        return jobDetails;
    }

    public void addJob(JobDetail jobDetail, CronTrigger trigger) throws SchedulerException {
        Scheduler scheduler = getScheduler();
        scheduler.scheduleJob(jobDetail, trigger);
    }

    public void addJob(Class jobClass, String jobName, String triggerName, String time) throws SchedulerException, ParseException {
        JobDetail jobDetail = newJob(jobClass)
                .withIdentity(jobName, DefaultJobGroup)
                .build();

        CronTrigger trigger = newTrigger()
                .withIdentity(triggerName, DefaultTriggerGroup)
                .withSchedule(CronScheduleBuilder.cronSchedule(time))
                .startNow()
                .build();

        getScheduler().start();

        addJob(jobDetail, trigger);
    }

    public void removeJob(String jobName, String triggerName) throws SchedulerException {
        Scheduler scheduler = getScheduler();

        TriggerKey triggerKey = new TriggerKey(triggerName, DefaultTriggerGroup);
        scheduler.pauseTrigger(triggerKey);
        scheduler.unscheduleJob(triggerKey);

        scheduler.deleteJob(new JobKey(jobName, DefaultJobGroup));
    }

    public void pauseJob(String jobName) {
        try {
            Scheduler scheduler = getScheduler();
            scheduler.pauseJob(new JobKey(jobName, DefaultJobGroup));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean hasJob(String jobName) throws SchedulerException {
        Scheduler scheduler = getScheduler();
        return scheduler.getJobDetail(new JobKey(jobName, DefaultTriggerGroup)) != null;
    }

    public List<Trigger> getTriggerList() throws SchedulerException {
        Scheduler scheduler = getScheduler();
        Set<TriggerKey> triggerKeySet = scheduler.getTriggerKeys(GroupMatcher.groupEquals(DefaultTriggerGroup));

        List<Trigger> triggers = new ArrayList<>();
        for (TriggerKey key : triggerKeySet){
            triggers.add(scheduler.getTrigger(key));
        }

        return triggers;
    }

    public void pauseTrigger(String jobName) {
        try {
            Scheduler scheduler = getScheduler();
            scheduler.pauseTrigger(new TriggerKey(jobName, DefaultTriggerGroup));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void resumeTrigger(String triggerName) throws SchedulerException {
        Scheduler scheduler = getScheduler();
        scheduler.resumeTrigger(new TriggerKey(triggerName, DefaultTriggerGroup));
    }

    public void updateTrigger(String jobName, String triggerName, String time) throws SchedulerException, ParseException {
        Scheduler scheduler = getScheduler();
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(new TriggerKey(triggerName, DefaultTriggerGroup));
        if (trigger == null) return;

        String oldTime = trigger.getCronExpression();
        if (!oldTime.equalsIgnoreCase(time)) {
            JobDetail jobDetail = scheduler.getJobDetail(new JobKey(jobName, DefaultJobGroup));
            Class jobCls = jobDetail.getJobClass();

            removeJob(jobName, triggerName);

            addJob(jobCls, jobName, triggerName, time);
        }
    }

    // inner methods
    private Scheduler getScheduler() throws SchedulerException {
        return ServiceFactory.getService(Services.SchedulerFactory);
    }
}
