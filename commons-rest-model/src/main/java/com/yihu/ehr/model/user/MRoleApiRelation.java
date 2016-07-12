package com.yihu.ehr.model.user;

/**
 * Created by yww on 2016/7/8.
 */
public class MRoleApiRelation {
    private long id;
    private long roleId;
    private long apiId;

    public MRoleApiRelation() {
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

    public long getApiId() {
        return apiId;
    }
    public void setApiId(long apiId) {
        this.apiId = apiId;
    }
}
