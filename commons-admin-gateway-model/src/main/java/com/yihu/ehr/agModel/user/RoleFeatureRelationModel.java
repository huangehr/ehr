package com.yihu.ehr.agModel.user;

/**
 * Created by yww on 2016/7/7.
 */
public class RoleFeatureRelationModel {
    private long id;
    private long roleId;
    private long featureId;

    public RoleFeatureRelationModel() {
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

    public long getFeatureId() {
        return featureId;
    }

    public void setFeatureId(long featureId) {
        this.featureId = featureId;
    }
}
