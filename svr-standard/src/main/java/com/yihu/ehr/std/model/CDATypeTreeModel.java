package com.yihu.ehr.std.model;

import java.util.List;

/**
 * Created by AndyCai on 2015/12/16.
 */
public class CDATypeTreeModel {
    private String id;
    private String code;
    private String name;
    private List<CDATypeTreeModel> children;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CDATypeTreeModel> getChildren() {
        return children;
    }

    public void setChildren(List<CDATypeTreeModel> children) {
        this.children = children;
    }
}
