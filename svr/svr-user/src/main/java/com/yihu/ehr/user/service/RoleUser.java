package com.yihu.ehr.user.service;

import javax.persistence.*;

/**
 * Created by yww on 2016/7/7.
 */
@Entity
@Table(name = "role_user")
@Access(value = AccessType.PROPERTY)
public class RoleUser {
    private long id;
    private long roleId;
    private String userId;

    public RoleUser() {}

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id",unique = true,nullable = false)
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "role_id",nullable = false)
    public long getRoleId() {
        return roleId;
    }
    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    @Column(name = "user_id",nullable = false)
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
}
