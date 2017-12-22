package com.yihu.ehr.org.model;


import com.yihu.ehr.entity.BaseIdentityEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

/**
 * 卫生机构类别 Entity
 *
 * @author 张进军
 * @date 2017/12/21 12:00
 */
@Entity
@Table(name = "org_health_category")
public class OrgHealthCategory extends BaseIdentityEntity {

    public Integer pid; // 父级主键
    public Integer topPid; // 顶级主键
    public String code; // 卫生机构类别编码
    public String name; // 卫生机构类别名称
    public String remark; // 备注

    // 临时字段
    private String text; // 名称，树形下拉框使用
    private List<OrgHealthCategory> children = new ArrayList<>(); // 子节点

    @Column(name = "pid")
    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    @Column(name = "top_pid")
    public Integer getTopPid() {
        return topPid;
    }

    public void setTopPid(Integer topPid) {
        this.topPid = topPid;
    }

    @Column(name = "code", nullable = false, unique = true)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "name", nullable = false, unique = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "remark")
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Transient
    public String getText() {
        return this.name;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Transient
    public List<OrgHealthCategory> getChildren() {
        return children;
    }

    public void setChildren(List<OrgHealthCategory> children) {
        this.children = children;
    }
}
