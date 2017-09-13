package com.yihu.ehr.agModel.user;

/**
 * Created by wxw on 2017/8/22.
 */
public class RoleReportRelationModel {
    private long id;
    private long roleId;
    private long rsReportId;

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

    public long getRsReportId() {
        return rsReportId;
    }

    public void setRsReportId(long rsReportId) {
        this.rsReportId = rsReportId;
    }
}
