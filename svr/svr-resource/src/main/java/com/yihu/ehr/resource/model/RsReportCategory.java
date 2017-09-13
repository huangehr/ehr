package com.yihu.ehr.resource.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 资源报表分类 entity
 *
 * @author 张进军
 * @created 2017.8.8 20:32
 */
@Entity
@Table(name = "rs_report_category")
public class RsReportCategory {

    private Integer id; // 主键
    private Integer pid; // 父级ID
    private String code; // 编码
    private String name; // 名称
    private String remark; // 备注

    // 临时字段
    private String text; // 名称，树形下拉框使用
    private List<RsReportCategory> children = new ArrayList<>(); // 子节点

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "pid")
    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    @Column(name = "code")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "name")
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
    public List<RsReportCategory> getChildren() {
        return children;
    }

    public void setChildren(List<RsReportCategory> children) {
        this.children = children;
    }

}
