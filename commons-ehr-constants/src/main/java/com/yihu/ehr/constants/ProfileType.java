package com.yihu.ehr.constants;

/**
 * @author linaz
 * @version 1.0
 * @created 2015.12.20 15:37
 */
public enum ProfileType {
    NonStructured(0),
    Structured(1),
    Link(2);

    private int type;
    ProfileType(int type){
        this.type = type;
    }

    public int getType(){
        return type;
    }
}
