package com.yihu.ehr.profile.core;

/**
 * @author Sand
 * @created 2016.04.26 19:43
 */
public enum EventType {
    Outpatient(0),
    Inpatient(1);

    int type;

    EventType(int type){
        this.type = type;
    }

    int getType(){
        return type;
    }
}
