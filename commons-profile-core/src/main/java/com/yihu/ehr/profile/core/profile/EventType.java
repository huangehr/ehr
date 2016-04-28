package com.yihu.ehr.profile.core.profile;

/**
 * @author Sand
 * @created 2016.04.26 19:43
 */
public enum EventType {
    Clinic(0),          // 门诊
    Resident(1);        // 住院

    int type;

    EventType(int type){
        this.type = type;
    }

    int getType(){
        return type;
    }
}
