package com.yihu.ehr.profile.core.profile;

/**
 * @author linaz
 * @version 1.0
 * @created 2015.12.20 15:37
 */
public enum ProfileType {
    Standard(10),
    Document(1),
    Link(2);

    private int type;
    ProfileType(int type){
        this.type = type;
    }

    public int getType(){
        return type;
    }
}
