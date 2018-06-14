package com.yihu.ehr.analyze.service.scheduler;

import com.yihu.ehr.analyze.job.WarningQuestionJob;
import com.yihu.ehr.analyze.service.dataQuality.DqPaltformResourceWarningService;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * 预警问题生成
 * @author yeshijie on 2018/6/13.
 */
@Service
public class WarningSchedulerService {

    private static final String WARNING_QUESTION = "WarningQuestion";
    private static final String WARNING_QUESTION_JOB = "WarningQuestionJob";
    private static final String WARNING_QUESTION_TRIGGER = "WarningQuestionTrigger";

    @Autowired
    private Scheduler scheduler;
    @Autowired
    private DqPaltformResourceWarningService dqPaltformResourceWarningService;

    /**
     * 初始化job
     */
    public void init(){
        try {
            String cronExp = dqPaltformResourceWarningService.getCronExp(null);
            GroupMatcher groupMatcher = GroupMatcher.groupEquals(WARNING_QUESTION);
            Set<JobKey> jobKeys = scheduler.getJobKeys(groupMatcher);
            //如果已经在执行了 就忽略
            if (null == jobKeys) {
                JobDetail jobDetail = newJob(WarningQuestionJob.class)
                        .withIdentity(WARNING_QUESTION_JOB, WARNING_QUESTION)
                        .build();
                CronTrigger trigger = newTrigger()
                        .withIdentity(WARNING_QUESTION_TRIGGER, WARNING_QUESTION)
                        .withSchedule(CronScheduleBuilder.cronSchedule(cronExp))
                        .startNow()
                        .build();
                scheduler.scheduleJob(jobDetail, trigger);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 新增job
     * @param cronExp (0 0 9 * * ?) 每天9:00点执行
     */
    public void addJob(String cronExp) {
        try {
            GroupMatcher groupMatcher = GroupMatcher.groupEquals(WARNING_QUESTION);
            Set<JobKey> jobKeys = scheduler.getJobKeys(groupMatcher);
            if (null != jobKeys) {
                //已经有定时任务在执行了 先停止旧任务
                for (JobKey jobKey : jobKeys) {
                    scheduler.deleteJob(jobKey);
                }
            }
            JobDetail jobDetail = newJob(WarningQuestionJob.class)
                    .withIdentity(WARNING_QUESTION_JOB, WARNING_QUESTION)
                    .build();
            CronTrigger trigger = newTrigger()
                    .withIdentity(WARNING_QUESTION_TRIGGER, WARNING_QUESTION)
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExp))
                    .startNow()
                    .build();
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除job
     */
    public void removeJob() {
        try {
            GroupMatcher groupMatcher = GroupMatcher.groupEquals(WARNING_QUESTION);
            Set<JobKey> jobKeys = scheduler.getJobKeys(groupMatcher);
            if (jobKeys != null) {
                for (JobKey jobKey : jobKeys) {
                    scheduler.deleteJob(jobKey);
                }
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
