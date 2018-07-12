package com.yihu.ehr.event.processor;

import com.yihu.ehr.event.model.Event;

/**
 * 基类 - 事件处理器
 * 1. 新增的事件处理器都必须继承此类
 * 2. 新增的事件处理器会在数据库 {@link com.yihu.ehr.entity.event.EventProcessor} 中生成相应的记录，并在添加时或者程序启动后加载进处理链中
 * Created by progr1mmer on 2018/7/4.
 */
public abstract class Processor {

    protected boolean active = true;
    protected String type; //事件类型

    public Processor(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public abstract void process(Event event);

}
