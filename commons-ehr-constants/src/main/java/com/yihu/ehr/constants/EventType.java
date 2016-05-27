package com.yihu.ehr.constants;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author Sand
 * @created 2016.04.26 19:43
 */
public enum EventType {
    Clinic(0),              // 门诊
    Resident(1),            // 住院
    MedicalExam(2);         // 体检

    int type;

    EventType(int type){
        this.type = type;
    }

    @JsonValue
    public int getType(){
        return type;
    }

    public static EventType create(String ordinal){
        return create(Integer.parseInt(ordinal));
    }

    public static EventType create(int ordinal){
        return EventType.values()[ordinal];
    }
}
