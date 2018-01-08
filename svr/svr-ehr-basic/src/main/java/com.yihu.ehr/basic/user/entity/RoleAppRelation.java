package com.yihu.ehr.basic.user.entity;

import javax.persistence.*;

/**
 * Created by yww on 2016/7/7.
 */
@Entity
@Table(name = "role_app_relation")
@Access(value = AccessType.PROPERTY)
public class RoleAppRelation {
    private long id;
    private long roleId;
    private String appId;

    public RoleAppRelation() {}

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

    @Column(name = "app_id",nullable = false)
    public String getAppId() {
        return appId;
    }
    public void setAppId(String appId) {
        this.appId = appId;
    }
}
