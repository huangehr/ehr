package com.yihu.ehr.analyze.service.scheduler;

import com.yihu.ehr.analyze.config.SchedulerConfig;
import com.yihu.ehr.analyze.job.PackageAnalyzeJob;
import io.swagger.annotations.ApiParam;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;
import java.util.UUID;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * @author Airhead
 * @created 2018-01-23
 */
@Service
public class SchedulerService {
    private static final String PACK_ANALYZER = "PackAnalyzer";
    private static final String PACK_ANALYZER_JOB = "PackAnalyzerJob-";
    private static final String PACK_ANALYZER_TRIGGER = "PackAnalyzerTrigger-";

    @Autowired
    private Scheduler scheduler;
    @Autowired
    private SchedulerConfig config;

    public ResponseEntity<String> updateScheduler(
            @ApiParam(name = "pause", value = "true:暂停 , false:执行", required = true, defaultValue = "true")
            @RequestParam(value = "pause") boolean pause) {
        try {
            if (pause) {
                scheduler.pauseAll();
            } else {
                scheduler.resumeAll();
            }
            return new ResponseEntity<>((String) null, HttpStatus.OK);
        } catch (SchedulerException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Integer> addJob(
            @ApiParam(name = "count", value = "任务数量（不要超过系统设定值）", required = true, defaultValue = "4")
            @RequestParam(value = "count") int count,
            @ApiParam(name = "cronExp", value = "触发器CRON表达式", required = true, defaultValue = "0/4 * * * * ?")
            @RequestParam(value = "cronExp") String cronExp) {
        try {
            if (count > config.getJobMaxSize()) {
                count = config.getJobMaxSize();
            }
            GroupMatcher groupMatcher = GroupMatcher.groupEquals(PACK_ANALYZER);
            Set<JobKey> jobKeys = scheduler.getJobKeys(groupMatcher);
            if (null != jobKeys) {
                int activeJob = jobKeys.size();
                for (int i = 0; i < count - activeJob; i++) {
                    String suffix = UUID.randomUUID().toString().substring(0, 8);
                    JobDetail jobDetail = newJob(PackageAnalyzeJob.class)
                            .withIdentity(PACK_ANALYZER_JOB + suffix, PACK_ANALYZER)
                            .build();
                    CronTrigger trigger = newTrigger()
                            .withIdentity(PACK_ANALYZER_TRIGGER + suffix, PACK_ANALYZER)
                            .withSchedule(CronScheduleBuilder.cronSchedule(cronExp))
                            .startNow()
                            .build();
                    scheduler.scheduleJob(jobDetail, trigger);
                }
            } else {
                for (int i = 0; i < count; i++) {
                    String suffix = UUID.randomUUID().toString().substring(0, 8);
                    JobDetail jobDetail = newJob(PackageAnalyzeJob.class)
                            .withIdentity(PACK_ANALYZER_JOB + suffix, PACK_ANALYZER)
                            .build();
                    CronTrigger trigger = newTrigger()
                            .withIdentity(PACK_ANALYZER_TRIGGER + suffix, PACK_ANALYZER)
                            .withSchedule(CronScheduleBuilder.cronSchedule(cronExp))
                            .startNow()
                            .build();
                    scheduler.scheduleJob(jobDetail, trigger);
                }
            }
            return new ResponseEntity<>(config.getJobMaxSize(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(-1, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> removeJob(
            @ApiParam(name = "count", value = "任务数量", required = true, defaultValue = "4")
            @RequestParam(value = "count") int count) {
        try {
            GroupMatcher groupMatcher = GroupMatcher.groupEquals(PACK_ANALYZER);
            Set<JobKey> jobKeySet = scheduler.getJobKeys(groupMatcher);
            if (jobKeySet != null) {
                for (JobKey jobKey : jobKeySet) {
                    scheduler.deleteJob(jobKey);
                    if (--count == 0) break;
                }
            }
            return new ResponseEntity<>((String) null, HttpStatus.OK);
        } catch (SchedulerException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Integer> count() {
        try {
            GroupMatcher groupMatcher = GroupMatcher.groupEquals(PACK_ANALYZER);
            Set<JobKey> jobKeySet = scheduler.getJobKeys(groupMatcher);
            int count = 0;
            if (jobKeySet != null) {
                count = jobKeySet.size();
            }
            return new ResponseEntity<>(count, HttpStatus.OK);
        } catch (SchedulerException e) {
            return new ResponseEntity<>(-1, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
