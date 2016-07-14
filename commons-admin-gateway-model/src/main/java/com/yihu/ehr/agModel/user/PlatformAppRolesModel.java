package com.yihu.ehr.agModel.user;

/**
 * Created by yww on 2016/7/13.
 */
public class PlatformAppRolesModel {
    private String id;
    private String appId;
    private String appName;
    private String roleId;
    private String roleName;

    public PlatformAppRolesModel(){
        //this.id =
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getAppId() {
        return appId;
    }
    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }
    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getRoleId() {
        return roleId;
    }
    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
