package com.yihu.ehr.agModel.standard.cdatype;

import java.util.List;

/**
 * Created by yww on 2016/3/18.
 */
public class CdaTypeTreeModel {
    private String id;
    private String code;
    private String name;
    private String parentId;
    private String description;
    private List<CdaTypeTreeModel> children;

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

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CdaTypeTreeModel> getChildren() {
        return children;
    }

    public void setChildren(List<CdaTypeTreeModel> children) {
        this.children = children;
    }

}
