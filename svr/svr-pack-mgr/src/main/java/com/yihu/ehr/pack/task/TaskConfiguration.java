package com.yihu.ehr.pack.task;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * 任务配置。Spring 4.2仍然无法通过Bean注入方式取得TaskScheduler，所以使用此方式获取任务注册器。
 *
 * @author Sand
 * @created 2016.05.12 15:00
 */
//@Configuration
@Deprecated
public class TaskConfiguration implements SchedulingConfigurer {

    private ScheduledTaskRegistrar taskRegistrar;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        this.taskRegistrar = taskRegistrar;
    }

    public ScheduledTaskRegistrar getTaskRegistrar() {
        return taskRegistrar;
    }
}