package com.yihu.ehr.controller;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.service.task.PackageResourceJob;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
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
 * @author Sand
 * @version 1.0
 * @created 2016.03.31 10:30
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "档案包解析调度任务", description = "档案包解析调度服务")
public class SchedulerEndPoint {
    @Autowired
    Scheduler scheduler;

    @ApiOperation(value = "设置任务调度器状态")
    @RequestMapping(value = ServiceApi.PackageResolve.Scheduler, method = RequestMethod.PUT)
    public ResponseEntity<String> updateScheduler(
            @ApiParam(name = "pause", value = "true:暂停 , false: 执行", defaultValue = "true")
            @RequestParam(value = "pause") boolean pause) {
        try {
            if (pause) {
                scheduler.pauseAll();
            } else {
                scheduler.resumeAll();
            }

            return new ResponseEntity<>((String) null, HttpStatus.OK);
        } catch (SchedulerException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @ApiOperation(value = "添加解析任务")
    @RequestMapping(value = ServiceApi.PackageResolve.Scheduler, method = RequestMethod.POST)
    public ResponseEntity<String> addJob(@ApiParam(name = "count", value = "任务数量", defaultValue = "8")
                                         @RequestParam(value = "count") int count,
                                         @ApiParam(name = "cronExp", value = "触发器CRON表达式", defaultValue = "0/2 * * * * ?")
                                         @RequestParam(value = "cronExp") String cronExp) {
        try {
            for (int i = 0; i < count; ++i) {
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
            }

            return new ResponseEntity<>((String) null, HttpStatus.OK);
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @ApiOperation(value = "删除解析任务")
    @RequestMapping(value = ServiceApi.PackageResolve.Scheduler, method = RequestMethod.DELETE)
    public ResponseEntity<String> removeJob(@ApiParam(name = "count", value = "任务数量", defaultValue = "8")
                                            @RequestParam(value = "count") int count) {
        try {
            Set<JobKey> jobKeySet = scheduler.getJobKeys(null);
            for (JobKey jobKey : jobKeySet) {
                scheduler.deleteJob(jobKey);

                if (--count == 0) break;
            }

            return new ResponseEntity<>((String) null, HttpStatus.OK);
        } catch (SchedulerException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
