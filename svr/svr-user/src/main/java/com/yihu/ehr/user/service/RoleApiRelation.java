package com.yihu.ehr.user.service;

import javax.persistence.*;

/**
 * Created by yww on 2016/7/8.
 */
@Entity
@Table(name = "role_api_relation")
@Access(AccessType.PROPERTY)
public class RoleApiRelation {
    private long id;
    private long roleId;
    private long apiId;

    public RoleApiRelation() {
    }

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

    @Column(name = "app_api_id",nullable = false)
    public long getApiId() {
        return apiId;
    }
    public void setApiId(long apiId) {
        this.apiId = apiId;
    }
}
