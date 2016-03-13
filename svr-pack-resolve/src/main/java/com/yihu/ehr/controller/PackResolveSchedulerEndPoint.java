package com.yihu.ehr.controller;

import com.yihu.ehr.constants.ApiVersion;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.12
 */
@RestController
@RequestMapping(ApiVersion.Version1_0 + "/schedulers")
@Api(protocols = "https", value = "Scheduler", description = "任务调度接口", tags = {"任务", "调度"})
public class PackResolveSchedulerEndPoint {
    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ApiOperation(value = "启动任务调度器", produces = "application/json", notes = "启动平台任务调度器，若调度器已关闭，则无法再开启")
    public Object startScheduler() {

    }

    @RequestMapping(value = "/", method = RequestMethod.DELETE)
    @ApiOperation(value = "停止任务调度器", produces = "application/json", notes = "关闭任务调度器，无法再开启")
    public Object stopScheduler(
            @ApiParam(value = "是否等任务完成再关闭", required = true)
            @RequestParam(value = "wait", required = true) Boolean wait) {

    }

    @RequestMapping(value = "/", method = RequestMethod.PUT)
    @ApiOperation(value = "暂停任务调度器", produces = "application/json", notes = "暂停任务调度器，可恢复运行")
    public Object standbyScheduler() {
        try {
            if (jobManager.isRunning()) {
                if (jobManager.isStandBy()) {
                    return new RestEcho().success().putMessage("任务管理器已暂停");
                } else {
                    jobManager.standbyScheduler();
                }
            } else if (jobManager.isShutdown()) {
                return new RestEcho().success().putMessage("任务管理器已关闭");
            }

            return new RestEcho().success().putMessage("ok");
        } catch (SchedulerException e) {
            return failed(ErrorCode.SchedulerShutdownFailed, e.getMessage());
        }
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ApiOperation(value = "恢复任务调度器", produces = "application/json", notes = "恢复任务调度器，可再暂停")
    public Object resumeScheduler() {
        try {
            if (jobManager.isRunning()) {
                if (jobManager.isStandBy()) {
                    jobManager.resumeScheduler();
                } else {
                    return new RestEcho().success().putMessage("任务管理器正在运行");
                }
            } else if (jobManager.isShutdown()) {
                return new RestEcho().success().putMessage("任务管理器已关闭");
            }

            return new RestEcho().success().putMessage("ok");
        } catch (SchedulerException e) {
            return failed(ErrorCode.SchedulerShutdownFailed, e.getMessage());
        }
    }

    @RequestMapping(value = "/jobs", method = RequestMethod.GET)
    @ApiOperation(value = "查看任务列表", produces = "application/json", notes = "查看内存中正在进行的任务列表")
    public Object getJobs() {
        try {
            ObjectNode root = objectMapper.createObjectNode();
            ArrayNode jobArray = root.putArray("jobs");

            List<JobDetail> jobDetails = jobManager.getJobList();
            for (JobDetail jobDetail : jobDetails){
                ObjectNode node = jobArray.addObject();
                node.put("name", jobDetail.getKey().getName());
                node.put("group", jobDetail.getKey().getGroup());
                node.put("description", jobDetail.getDescription());
                node.put("class", jobDetail.getJobClass().getName());
            }

            return new RestEcho().success().putResult(root);
        } catch (SchedulerException e) {
            return failed(ErrorCode.GetJobDetailFailed, e.getMessage());
        }
    }

    @RequestMapping(value = "/triggers", method = RequestMethod.GET)
    @ApiOperation(value = "查看触发器列表", produces = "application/json", notes = "查看内存中正在进行的触发器列表")
    public Object getTriggers() {
        try {
            ObjectNode root = objectMapper.createObjectNode();
            ArrayNode triggersArray = root.putArray("triggers");

            List<Trigger> triggers = jobManager.getTriggerList();
            for (Trigger trigger : triggers){
                ObjectNode node = triggersArray.addObject();
                node.put("name", trigger.getKey().getName());
                node.put("group", trigger.getKey().getGroup());
                node.put("description", trigger.getDescription());
                node.put("start_time", trigger.getStartTime().toString());
                node.put("next_fire_time", trigger.getNextFireTime().toString());
                node.put("end_time", trigger.getEndTime() == null ? null : trigger.getEndTime().toString());
                node.put("calendar", trigger.getCalendarName());
                node.put("priority", trigger.getPriority());
            }

            return new RestEcho().success().putResult(root);
        } catch (SchedulerException e) {
            return failed(ErrorCode.GetTriggerFailed, e.getMessage());
        }
    }

    @RequestMapping(value = "/job", method = RequestMethod.POST)
    @ApiOperation(value = "添加任务", produces = "application/json", notes = "目前仅支持预定义任务：档案归档与索引创建")
    public Object addJob(
            @ApiParam(value = "任务类型", required = true)
            @RequestParam(value = "type", required = true) JobType jobType,
            @ApiParam(value = "任务名称", defaultValue = "job.1", required = true)
            @RequestParam(value = "job_name", required = true) String jobName,
            @ApiParam(value = "触发器名称", defaultValue = "trigger.1", required = true)
            @RequestParam(value = "trigger_name", required = true) String triggerName,
            @ApiParam(value = "触发器Cron表达式", defaultValue = "0/2 * * * * ?", required = true)
            @RequestParam(value = "cron_expression", required = true) String cronExp) {
        try {
            if (!jobManager.hasJob(jobType.toString())) {
                Class jobCls = getJobClass(jobType);
                jobManager.addJob(jobCls, jobName, triggerName, cronExp);
            }

            return new RestEcho().success().putMessage("ok");
        } catch (SchedulerException e) {
            return failed(ErrorCode.SchedulerAddJobFailed, e.getMessage());
        } catch (ParseException e) {
            return failed(ErrorCode.SchedulerAddJobFailed, e.getMessage());
        }
    }

    @RequestMapping(value = "/job", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除任务", produces = "application/json", notes = "删除任务")
    public Object removeJob(
            @ApiParam(value = "任务类型", required = true)
            @RequestParam(value = "type", required = true) JobType jobType) {
        try {
            jobManager.removeJob(jobType.toString(), jobType.toString());
            return new RestEcho().success().putMessage("ok");
        } catch (SchedulerException e) {
            return failed(ErrorCode.SchedulerRemoveJobFailed, e.getMessage());
        }
    }

    @RequestMapping(value = "/job_action", method = RequestMethod.GET)
    @ApiOperation(value = "启动任务", produces = "application/json", notes = "启动任务")
    public Object startJob(
            @ApiParam(value = "任务类型", required = true)
            @RequestParam(value = "type", required = true) JobType jobType) {
        try {
            jobManager.resumeTrigger(jobType.toString());

            return new RestEcho().success().putMessage("ok");
        } catch (SchedulerException e) {
            return failed(ErrorCode.SchedulerRemoveJobFailed, e.getMessage());
        }
    }

    @RequestMapping(value = "/job_action", method = RequestMethod.POST)
    @ApiOperation(value = "暂停任务", produces = "application/json", notes = "暂停任务")
    public Object pauseJob(
            @ApiParam(value = "任务类型", required = true)
            @RequestParam(value = "type", required = true) JobType jobType) {
        jobManager.pauseJob(jobType.toString());

        return new RestEcho().success().putMessage("ok");
    }

    @RequestMapping(value = "/job_action", method = RequestMethod.PUT)
    @ApiOperation(value = "恢复任务", produces = "application/json", notes = "恢复任务")
    public Object resumeJob(
            @ApiParam(value = "任务类型", required = true)
            @RequestParam(value = "type", required = true) JobType jobType) {
        try {
            jobManager.resumeTrigger(jobType.toString());
            return new RestEcho().success().putMessage("ok");
        } catch (SchedulerException e) {
            return failed(ErrorCode.SchedulerRemoveJobFailed, e.getMessage());
        }
    }
}
