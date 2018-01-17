package com.yihu.quota.model.rest;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/1/16.
 */
public class HospitalComposeModel implements Serializable {
    private String title;
    private List<Map<String, Object>> listMap;
    private HospitalComposeModel children;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Map<String, Object>> getListMap() {
        return listMap;
    }

    public void setListMap(List<Map<String, Object>> listMap) {
        this.listMap = listMap;
    }

    public HospitalComposeModel getChildren() {
        return children;
    }

    public void setChildren(HospitalComposeModel children) {
        this.children = children;
    }
}
