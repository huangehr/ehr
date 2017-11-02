package com.yihu.ehr.constants;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 档案类型。注意新增类型只能依次增加，不能插队。
 * 1结构化档案，文件档案，链接档案，数据集档案
 */
public enum ProfileType {
    Standard(1),
    File(2),
    Link(3),
    DataSet(4);

    private int type;

    ProfileType(int type){
        this.type = type;
    }

    @JsonValue
    public int getType(){
        return type;
    }

    public String toString(){
        return Integer.toString(type);
    }

    public static ProfileType create(String ordinal){
        return create(Integer.parseInt(ordinal));
    }

    public static ProfileType create(int ordinal){
        return ProfileType.values()[ordinal];
    }
}
