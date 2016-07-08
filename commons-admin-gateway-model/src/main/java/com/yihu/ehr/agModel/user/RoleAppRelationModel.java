package com.yihu.ehr.agModel.user;

/**
 * Created by yww on 2016/7/7.
 */
public class RoleAppRelationModel {
    private long id;
    private long roleId;
    private String roleName;
    private String appId;

    public RoleAppRelationModel() {
    }

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

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
}
