package com.yihu.ehr.user.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by wxw on 2017/8/22.
 */
@Entity
@Table(name = "role_report_relation")
@Access(value = AccessType.PROPERTY)
public class RoleReportRelation implements Serializable{
    private long id;
    private long roleId;
    private long rsReportId;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",unique = true,nullable = false)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "ROLE_ID",nullable = false)
    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    @Column(name = "RS_REPORT_ID",nullable = false)
    public long getRsReportId() {
        return rsReportId;
    }

    public void setRsReportId(long rsReportId) {
        this.rsReportId = rsReportId;
    }
}
