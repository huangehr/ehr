package com.yihu.ehr.agModel.user;

/**
 * Created by yww on 2016/7/8.
 */
public class RoleApiRelationModel {
    private long id;
    private long roleId;
    private String roleName;
    private long apiId;
    private String apiName;

    public RoleApiRelationModel() {
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

    public long getApiId() {
        return apiId;
    }
    public void setApiId(long apiId) {
        this.apiId = apiId;
    }

    public String getApiName() {
        return apiName;
    }
    public void setApiName(String apiName) {
        this.apiName = apiName;
    }
}
