package com.yihu.ehr.service;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import java.text.ParseException;
import java.util.List;

/**
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.27 16:02
 */
public interface XJobManager {
    // schedulers
    public void startScheduler() throws SchedulerException;

    public void shutdownScheduler(boolean waitForJobsToComplete) throws SchedulerException;

    public void standbyScheduler() throws SchedulerException;

    public void resumeScheduler() throws SchedulerException;

    public boolean isRunning() throws SchedulerException;

    public boolean isStandBy() throws SchedulerException;

    public boolean isShutdown() throws SchedulerException;

    // job
    public List<JobDetail> getJobList() throws SchedulerException;

    public void addJob(JobDetail job, CronTrigger trigger) throws SchedulerException;

    public void addJob(Class jobClass, String jobName, String triggerName, String time) throws SchedulerException, ParseException;

    public void removeJob(String jobName, String triggerName) throws SchedulerException;

    public void pauseJob(String jobName);

    public boolean hasJob(String jobName) throws SchedulerException;

    // trigger
    public List<Trigger> getTriggerList() throws SchedulerException;

    public void updateTrigger(String jobName, String triggerName, String time) throws SchedulerException, ParseException;

    public void pauseTrigger(String jobName);

    public void resumeTrigger(String triggerName) throws SchedulerException;
}
