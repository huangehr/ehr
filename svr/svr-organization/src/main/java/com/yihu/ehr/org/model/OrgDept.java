package com.yihu.ehr.org.model;

import javax.persistence.*;

/**
 * Created by Dell on 2017/2/13.
 */
@Entity
@Table(name = "org_dept", schema = "", catalog = "healtharchive")
public class OrgDept {
    private int id;
    private int parentDeptId;
    private String orgId;
    private String code;
    private String name;
    private Integer delFlag;
    private Integer sortNo;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id",unique = true,nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "parent_dept_id", nullable = false, insertable = true, updatable = true)
    public int getParentDeptId() {
        return parentDeptId;
    }

    public void setParentDeptId(int parentDeptId) {
        this.parentDeptId = parentDeptId;
    }

    @Column(name = "org_id", nullable = false, insertable = true, updatable = true)
    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    @Column(name = "code", nullable = true)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "name", nullable = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "del_flag", nullable = true)
    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    @Column(name = "sort_no", nullable = true)
    public Integer getSortNo() {
        return sortNo;
    }

    public void setSortNo(Integer sortNo) {
        this.sortNo = sortNo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrgDept orgDept = (OrgDept) o;

        if (id != orgDept.id) return false;
        if (parentDeptId != orgDept.parentDeptId) return false;
        if (orgId != null ? !orgId.equals(orgDept.orgId) : orgDept.orgId != null) return false;
        if (code != null ? !code.equals(orgDept.code) : orgDept.code != null) return false;
        if (name != null ? !name.equals(orgDept.name) : orgDept.name != null) return false;
        if (delFlag != null ? !delFlag.equals(orgDept.delFlag) : orgDept.delFlag != null) return false;
        if (sortNo != null ? !sortNo.equals(orgDept.sortNo) : orgDept.sortNo != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + parentDeptId;
        result = 31 * result + (orgId != null ? orgId.hashCode() : 0);
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (delFlag != null ? delFlag.hashCode() : 0);
        result = 31 * result + (sortNo != null ? sortNo.hashCode() : 0);
        return result;
    }
}