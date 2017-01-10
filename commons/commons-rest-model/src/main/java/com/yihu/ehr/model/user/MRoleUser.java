package com.yihu.ehr.model.user;

/**
 * Created by yww on 2016/7/7.
 */
public class MRoleUser {
    private long id;
    private long roleId;
    private String userId;

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

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
}
