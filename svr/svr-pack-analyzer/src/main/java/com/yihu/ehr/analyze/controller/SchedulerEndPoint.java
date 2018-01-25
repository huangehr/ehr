package com.yihu.ehr.analyze.controller;

import com.yihu.ehr.analyze.service.scheduler.SchedulerService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Airhead
 * @version 1.0
 * @created 2016.01.18
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "SchedulerEndPoint", description = "档案分析任务", tags = {"档案分析服务-档案分析任务"})
public class SchedulerEndPoint extends EnvelopRestEndPoint {
    @Autowired
    private SchedulerService schedulerService;

    @ApiOperation(value = "设置任务调度器状态")
    @RequestMapping(value = ServiceApi.PackageAnalyzer.Scheduler, method = RequestMethod.PUT)
    public ResponseEntity<String> updateScheduler(
            @ApiParam(name = "pause", value = "true:暂停 , false:执行", required = true, defaultValue = "true")
            @RequestParam(value = "pause") boolean pause) {
        return schedulerService.updateScheduler(pause);
    }

    @ApiOperation(value = "调整当前任务数量，返回当前系统最大任务限制数")
    @RequestMapping(value = ServiceApi.PackageAnalyzer.Scheduler, method = RequestMethod.POST)
    public ResponseEntity<Integer> addJob(
            @ApiParam(name = "count", value = "任务数量（不要超过系统设定值）", required = true, defaultValue = "4")
            @RequestParam(value = "count") int count,
            @ApiParam(name = "cronExp", value = "触发器CRON表达式", required = true, defaultValue = "0/4 * * * * ?")
            @RequestParam(value = "cronExp") String cronExp) {
        return schedulerService.addJob(count, cronExp);
    }

    @ApiOperation(value = "删除解析任务")
    @RequestMapping(value = ServiceApi.PackageAnalyzer.Scheduler, method = RequestMethod.DELETE)
    public ResponseEntity<String> removeJob(
            @ApiParam(name = "count", value = "任务数量", required = true, defaultValue = "4")
            @RequestParam(value = "count") int count) {
        return schedulerService.removeJob(count);
    }

    @ApiOperation(value = "获取当前任务数量")
    @RequestMapping(value = ServiceApi.PackageAnalyzer.Scheduler, method = RequestMethod.GET)
    public ResponseEntity<Integer> count() {
        return schedulerService.count();
    }
}
