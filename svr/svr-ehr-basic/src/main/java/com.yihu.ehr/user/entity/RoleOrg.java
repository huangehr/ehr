package com.yihu.ehr.user.entity;

import javax.persistence.*;

/**
 * Created by janseny 2017年10月18日
 */
@Entity
@Table(name = "role_org")
@Access(value = AccessType.PROPERTY)
public class RoleOrg {
    private long id;
    private long roleId;
    private String orgCode;

    public RoleOrg() {}

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

    @Column(name = "org_code",nullable = false)
    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }
}
