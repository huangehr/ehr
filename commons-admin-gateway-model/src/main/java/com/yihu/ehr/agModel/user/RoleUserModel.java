package com.yihu.ehr.agModel.user;

/**
 * Created by yww on 2016/7/7.
 */
public class RoleUserModel {
    private long id;
    private long roleId;
    private String userId;
    private String userName;

    public RoleUserModel() {
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
