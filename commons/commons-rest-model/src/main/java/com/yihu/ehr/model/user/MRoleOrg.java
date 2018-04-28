package com.yihu.ehr.model.user;

import java.io.Serializable;

/**
 * Created by janseny 2017年10月18日
 */
public class MRoleOrg implements Serializable{
    private long id;
    private long roleId;
    private String orgCode;
    private String  orgName;

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

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
}
