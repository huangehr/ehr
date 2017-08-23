package com.yihu.quota.model.rest;

import java.util.List;

/**
 * Created by janseny on 2017/7/2.
 */
public class ReultModel {

    private List<String> cloumns ;
    private Object value;

    public List<String> getCloumns() {
        return cloumns;
    }

    public void setCloumns(List<String> cloumns) {
        this.cloumns = cloumns;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
