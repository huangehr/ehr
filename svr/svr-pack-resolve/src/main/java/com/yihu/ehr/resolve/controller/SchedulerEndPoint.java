package com.yihu.ehr.resolve.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.resolve.config.SchedulerConfig;
import com.yihu.ehr.resolve.job.SchedulerManager;
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

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.31 10:30
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "SchedulerEndPoint", description = "资源化入库任务", tags = {"档案解析服务-资源化入库任务"})
public class SchedulerEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private SchedulerConfig schedulerConfig;
    @Autowired
    private SchedulerManager schedulerManager;
    @Autowired
    private Scheduler scheduler;

    @ApiOperation(value = "设置任务调度器状态")
    @RequestMapping(value = ServiceApi.PackageResolve.Scheduler, method = RequestMethod.PUT)
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

    @ApiOperation(value = "添加任务数量，返回当前系统最大任务限制数")
    @RequestMapping(value = ServiceApi.PackageResolve.Scheduler, method = RequestMethod.POST)
    public ResponseEntity<Integer> addJob(
            @ApiParam(name = "count", value = "任务数量（不要超过系统设定值）", required = true, defaultValue = "4")
            @RequestParam(value = "count") int count,
            @ApiParam(name = "cronExp", value = "触发器CRON表达式", required = true, defaultValue = "0/1 * * * * ?")
            @RequestParam(value = "cronExp") String cronExp) throws Exception {
        if (count > schedulerConfig.getMaxSize()) {
            count = schedulerConfig.getMaxSize();
        }
        schedulerManager.addJob(count, cronExp);
        return new ResponseEntity<>(schedulerConfig.getMaxSize(), HttpStatus.OK);
    }

    @ApiOperation(value = "删除解析任务")
    @RequestMapping(value = ServiceApi.PackageResolve.Scheduler, method = RequestMethod.DELETE)
    public ResponseEntity<String> removeJob(
            @ApiParam(name = "count", value = "任务数量", required = true, defaultValue = "4")
            @RequestParam(value = "count") int count) throws Exception {
        schedulerManager.minusJob(count);
        return new ResponseEntity<>((String) null, HttpStatus.OK);
    }

    @ApiOperation(value = "获取当前任务数量")
    @RequestMapping(value = ServiceApi.PackageResolve.Scheduler, method = RequestMethod.GET)
    public ResponseEntity<Integer> count() throws Exception {
        int count = schedulerManager.getJobSize();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }
}
