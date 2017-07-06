package com.yihu.quota.util;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.Map;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;


@Component("quartzHelper")
public class QuartzHelper {
    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    private Scheduler scheduler = null;

    @PostConstruct
    public void init() {
        try {
            scheduler = schedulerFactoryBean.getScheduler();
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public void addJob(Class jobClass, String cronString, String jobKey,
                       Map<String, Object> params) throws Exception {
        if (!CronExpression.isValidExpression(cronString)) {
            throw new Exception("cronExpression is not a valid Expression");
        }
        try {
            JobDetail job = JobBuilder.newJob(jobClass)
                    .withIdentity("job-id:" + jobKey, "job-group:" + jobKey)
                    .build();
            JobDataMap jobDataMap = job.getJobDataMap();
            jobDataMap.putAll(params);
            CronTrigger trigger = TriggerBuilder
                    .newTrigger()
                    .withIdentity("trigger-name:" + jobKey,
                            "trigger-group:" + jobKey)
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronString))
                    .build();
            scheduler.scheduleJob(job, trigger);
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public void removeJob(String jobKeyString) throws Exception {
        TriggerKey triggerKey = new TriggerKey("trigger-name:" + jobKeyString,
                "trigger-group:" + jobKeyString);
        JobKey jobName = new JobKey("job-group:" + jobKeyString, "job-id:"
                + jobKeyString);
        scheduler.pauseTrigger(triggerKey);// 停止触发器
        scheduler.unscheduleJob(triggerKey);// 移除触发器
        scheduler.deleteJob(jobName);// 删除任务
    }

    public boolean isExistJob(String jobKey) throws SchedulerException {
        JobKey jk = new JobKey("job-id:" + jobKey, "job-group:" + jobKey);
        if (scheduler.checkExists(jk)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 立即执行任务。
     *
     * @param jobClass
     * @param id
     * @param params
     * @throws Exception
     */
    public void startNow(Class jobClass, String id, Map<String, Object> params) throws Exception {
        startAt(new Date(), jobClass, id, params);
    }

    /**
     * 在指定时间点执行。
     *
     * @param time
     * @param jobClass
     * @param id
     * @param params
     * @throws Exception
     */
    public void startAt(Date time, Class jobClass, String id, Map<String, Object> params) throws Exception {
        JobDetail job = JobBuilder.newJob(jobClass).
                withIdentity("job-id:" + id, "job-group:" + id)
                .build();
        JobDataMap jobDataMap = job.getJobDataMap();
        if(null != params) jobDataMap.putAll(params);

        SimpleTrigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger-id:" + id, "group-group:" + id)
                .startAt(time)
                .withSchedule(simpleSchedule().withIntervalInSeconds(10).withRepeatCount(0).withMisfireHandlingInstructionFireNow())
                .build();
        scheduler.scheduleJob(job, trigger);
        scheduler.start();
    }
}
