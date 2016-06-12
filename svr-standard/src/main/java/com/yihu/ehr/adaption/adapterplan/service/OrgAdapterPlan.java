package com.yihu.ehr.adaption.adapterplan.service;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * 机构采集标准信息表
 *
 * @author lincl
 * @version 1.0
 * @created 2016.2.3
 */
@Entity
@Table(name = "org_adapter_plan")
public class OrgAdapterPlan {

    /**
     * 标准版本
     */
    private String version;
    /**
     * 方案代码
     */
    private String code;
    /**
     * 说明
     */
    private String description;
    private Long id;
    /**
     * 方案名称
     */
    private String name;
    /**
     * 适配机构
     */
    private String org;
    /**
     * 父级方案ID
     */
    private Long parentId;
    /**
     * 方案类别
     */
    private String type;

    private int status;

    public OrgAdapterPlan() {
        this.status = 0;
    }

    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "increment")
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "cda_version", unique = false, nullable = false)
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Column(name = "code", unique = false, nullable = false)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "description", unique = false, nullable = true)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "name", unique = false, nullable = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "org", unique = false, nullable = true)
    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    @Column(name = "parent_id", unique = false, nullable = true)
    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    @Column(name = "type", unique = false, nullable = false)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(name = "status", unique = false, nullable = false)
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}