package com.yihu.ehr.profile;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @Author: zhengwei
 * @Date: 2018/5/25 18:01
 * @Description:质控错误类型
 */
public enum ErrorType {
    Normal(0, "验证正常"),
    EmptyError(1, "字段值为空"),
    ValueError(2, "值域超出"),
    TypeError(3, "类型错误"),
    FormatError(4, "格式错误"),
    FieldAdaptationError(5, "资源适配错误"),
    DictAdaptationError(6, "字典适配错误");


    private String name;
    private int type;

    ErrorType(int type, String name){
        this.type = type;
        this.name = name;
    }

    @JsonValue
    public int getType(){
        return type;
    }

    @JsonValue
    public String getName(){
        return name;
    }

    public static ErrorType create(String ordinal){
        return create(Integer.parseInt(ordinal));
    }

    public static ErrorType create(int ordinal){
        return ErrorType.values()[ordinal];
    }
}
