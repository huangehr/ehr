package com.yihu.ehr.event.model;


/**
 * Created by progr1mmer on 2018/7/4.
 */
public class Event {

    private final String type; //事件类型
    private final String value; //事件信息

    public Event(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
