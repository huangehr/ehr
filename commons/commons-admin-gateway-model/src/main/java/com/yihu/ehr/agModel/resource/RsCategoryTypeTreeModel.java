package com.yihu.ehr.agModel.resource;

import java.util.List;

/**
 * Created by yww on 2016/3/18.
 */
public class RsCategoryTypeTreeModel {
    private String id;
    private String name;
    private String pid;
    private String description;
    private List<RsCategoryTypeTreeModel> children;
    private  List<RsResourcesModel>  rsResourceslist;
    private String resourceIds;
    private String resourceCode;
    private boolean ischecked = false; // 是否选中（报表视图配置用到）

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
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

    public String getResourceIds() {
        return resourceIds;
    }

    public void setResourceIds(String resourceIds) {
        this.resourceIds = resourceIds;
    }

    public String getResourceCode() {
        return resourceCode;
    }

    public void setResourceCode(String resourceCode) {
        this.resourceCode = resourceCode;
    }

    public List<RsResourcesModel> getRsResourceslist() {
        return rsResourceslist;
    }

    public void setRsResourceslist(List<RsResourcesModel> rsResourceslist) {
        this.rsResourceslist = rsResourceslist;
    }

    public boolean getIschecked() {
        return ischecked;
    }

    public void setIschecked(boolean ischecked) {
        this.ischecked = ischecked;
    }
}
