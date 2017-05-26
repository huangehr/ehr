package com.yihu.ehr.agModel.org;

import java.util.List;

/**
 * @author zdm
 * @vsrsion 1.0
 * Created at 2017/5/25
 */
public class OrgDeptListModel {

    private int id;
    private String orgId;
    private String orgName;
    private List<OrgDeptModel> orgDept;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public List<OrgDeptModel> getOrgDept() {
        return orgDept;
    }

    public void setOrgDept(List<OrgDeptModel> orgDept) {
        this.orgDept = orgDept;
    }
}
