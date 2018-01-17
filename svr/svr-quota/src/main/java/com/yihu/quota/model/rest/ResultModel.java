package com.yihu.quota.model.rest;

import java.util.List;

/**
 * Created by janseny on 2017/7/2.
 */
public class ResultModel {

    private List<String> cloumns ;//维度对应的字典项名称 如：维度为org时 值为 余干县人民医院 ：维度为town时 值为 余干县
    private Object value;

    public List<String> getCloumns() {
        return cloumns;
    }

    public Object getValue() {
        return value;
    }

    public void setCloumns(List<String> cloumns) {
        this.cloumns = cloumns;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
