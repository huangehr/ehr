package com.yihu.ehr.model.user;

/**
 * Created by yww on 2016/7/7.
 */
public class MRoleFeatureRelation {
    private long id;
    private long roleId;
    private long featureId;

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
