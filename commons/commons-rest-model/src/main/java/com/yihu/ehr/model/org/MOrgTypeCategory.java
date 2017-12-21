package com.yihu.ehr.model.org;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.yihu.ehr.model.resource.MRsReportCategory;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 机构类型管理
 *
 * @author 张进军
 * @date 2017/12/21 12:00
 */
public class MOrgTypeCategory implements Serializable {

    public Integer id; // 主键
    public Integer pid; // 父级主键
    public Integer topPid; // 顶级主键
    public String code; // 机构类型编码
    public String name; // 机构类型名称
    public String remark; // 备注
    public Date createDate; // 创建时间
    public String creator; // 创建者
    public Date modifyDate; // 修改时间
    public String modifier; // 修改者
    private String text; // 名称：树形下拉框使用
    private List<MRsReportCategory> children; // 子节点

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public Integer getTopPid() {
        return topPid;
    }

    public void setTopPid(Integer topPid) {
        this.topPid = topPid;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<MRsReportCategory> getChildren() {
        return children;
    }

    public void setChildren(List<MRsReportCategory> children) {
        this.children = children;
    }
}
