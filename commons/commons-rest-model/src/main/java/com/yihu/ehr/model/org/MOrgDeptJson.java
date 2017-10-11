package com.yihu.ehr.model.org;

import java.io.Serializable;

/**
 * Created by wxw on 2017/10/10.
 */
public class MOrgDeptJson implements Serializable{
    private String orgId;
    private String deptIds;

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getDeptIds() {
        return deptIds;
    }

    public void setDeptIds(String deptIds) {
        this.deptIds = deptIds;
    }
}
