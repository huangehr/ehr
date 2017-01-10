package com.yihu.ehr.agModel.user;

import java.util.List;

/**
 * Created by Administrator on 2016/7/14.
 */
public class PlatformAppRolesTreeModel {
    private String id;// 应用id或角色组id
    private String name; //应用名称或角色组名称
    private String type; //0或1，表示该模型是应用还是角色
    private String pid;
    private List<PlatformAppRolesTreeModel> children;

    public PlatformAppRolesTreeModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public List<PlatformAppRolesTreeModel> getChildren() {
        return children;
    }

    public void setChildren(List<PlatformAppRolesTreeModel> children) {
        this.children = children;
    }
}
