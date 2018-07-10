package com.yihu.ehr.event.processor;

import com.yihu.ehr.event.model.Event;

/**
 * Created by progr1mmer on 2018/7/4.
 */
public abstract class Processor {

    protected boolean active = true;
    protected String type; //事件类型

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
