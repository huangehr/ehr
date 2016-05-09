package com.yihu.ehr.profile.core;

/**
 * 档案类型。注意新增类型只能依次增加，不能插队。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.12.20 15:37
 */
public enum ProfileType {
    Standard(0),
    Document(1),
    Link(2);

    private int type;
    ProfileType(int type){
        this.type = type;
    }

    public int getType(){
        return type;
    }

    public String toString(){
        return Integer.toString(type);
    }

    public static ProfileType create(String ordinal){
        return create(Integer.getInteger(ordinal));
    }

    public static ProfileType create(int ordinal){
        return ProfileType.values()[ordinal];
    }
}
