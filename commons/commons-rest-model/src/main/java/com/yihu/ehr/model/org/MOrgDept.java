package com.yihu.ehr.model.org;

import java.io.Serializable;

/**
 *  机构下部门 model
 * @author HZY
 * @vsrsion 1.0
 * Created at 2017/2/15.
 */
public class MOrgDept implements Serializable {
    private int id;
    private int parentDeptId;
    private String orgId;
    private String code;
    private String name;
    private Integer delFlag;
    private Integer sortNo;

    private MOrgDeptDetail deptDetail;

    public MOrgDeptDetail getDeptDetail() {
        return deptDetail;
    }

    public void setDeptDetail(MOrgDeptDetail deptDetail) {
        this.deptDetail = deptDetail;
    }

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
