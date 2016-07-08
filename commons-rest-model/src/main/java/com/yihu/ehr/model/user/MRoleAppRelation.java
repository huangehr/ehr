package com.yihu.ehr.model.user;

/**
 * Created by yww on 2016/7/7.
 */
public class MRoleAppRelation {
    private long id;
    private long roleId;
    private String appId;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public long getRoleId() {
        return roleId;
    }
    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    public String getAppId() {
        return appId;
    }
    public void setAppId(String appId) {
        this.appId = appId;
    }
}
