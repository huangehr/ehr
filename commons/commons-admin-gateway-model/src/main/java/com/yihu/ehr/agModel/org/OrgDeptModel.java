package com.yihu.ehr.agModel.org;

/**
 * @author HZY
 * @vsrsion 1.0
 * Created at 2017/2/20.
 */
public class OrgDeptModel {

    private int id;
    private int parentDeptId;
    private String orgId;
    private String code;
    private String name;
    private Integer delFlag;
    private Integer sortNo;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentDeptId() {
        return parentDeptId;
    }

    public void setParentDeptId(int parentDeptId) {
        this.parentDeptId = parentDeptId;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public Integer getSortNo() {
        return sortNo;
    }

    public void setSortNo(Integer sortNo) {
        this.sortNo = sortNo;
    }
}
