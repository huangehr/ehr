package com.yihu.ehr.analyze.controller;

import com.yihu.ehr.analyze.job.PackageAnalyzeJob;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.UUID;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * @author Airhead
 * @version 1.0
 * @created 2016.01.18
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "SchedulerEndPoint", description = "档案分析任务", tags = {"档案分析服务-档案分析任务"})
public class SchedulerEndPoint extends EnvelopRestEndPoint {

    public static final String PACK_ANALYZER = "PackAnalyzer";
    public static final String PACK_ANALYZER_JOB = "PackAnalyzerJob-";
    public static final String PACK_ANALYZER_TRIGGER = "PackAnalyzerTrigger-";
    @Value("${analyze.job.max-size}")
    private int jobMaxSize;
    @Autowired
    private Scheduler scheduler;

    @ApiOperation(value = "设置任务调度器状态")
    @RequestMapping(value = ServiceApi.PackageAnalyzer.Scheduler, method = RequestMethod.PUT)
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

    @ApiOperation(value = "调整当前任务数量，返回当前系统最大任务限制数")
    @RequestMapping(value = ServiceApi.PackageAnalyzer.Scheduler, method = RequestMethod.POST)
    public ResponseEntity<Integer> addJob(
            @ApiParam(name = "count", value = "任务数量（不要超过系统设定值）", required = true, defaultValue = "4")
            @RequestParam(value = "count") int count,
            @ApiParam(name = "cronExp", value = "触发器CRON表达式", required = true, defaultValue = "0/4 * * * * ?")
            @RequestParam(value = "cronExp") String cronExp) {
        try {
            if (count > jobMaxSize) {
                count = jobMaxSize;
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
            return new ResponseEntity<>(jobMaxSize, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(-1, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "删除解析任务")
    @RequestMapping(value = ServiceApi.PackageAnalyzer.Scheduler, method = RequestMethod.DELETE)
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

    @ApiOperation(value = "获取当前任务数量")
    @RequestMapping(value = ServiceApi.PackageAnalyzer.Scheduler, method = RequestMethod.GET)
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
