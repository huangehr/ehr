package com.yihu.ehr.agModel.resource;

import java.util.List;

/**
 * Created by yww on 2016/3/18.
 */
public class RsCategoryTypeTreeModel {
    private String id;
    private String name;
    private String pId;
    private String description;
    private List<RsCategoryTypeTreeModel> children;
    private List<RsResourcesModel> resourceIds;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
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

    public List<RsCategoryTypeTreeModel> getChildren() {
        return children;
    }

    public void setChildren(List<RsCategoryTypeTreeModel> children) {
        this.children = children;
    }

    public List<RsResourcesModel> getResourceIds() {
        return resourceIds;
    }

    public void setResourceIds(List<RsResourcesModel> resourceIds) {
        this.resourceIds = resourceIds;
    }
}
