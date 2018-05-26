package com.yihu.ehr.profile;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @Author: zhengwei
 * @Date: 2018/5/25 18:01
 * @Description:质控错误类型
 */
public enum ErrorType {
    EmptyError(1),
    ValueError(2),
    TypeError(3),
    FormatError(4);

    int type;

    ErrorType(int type){
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
