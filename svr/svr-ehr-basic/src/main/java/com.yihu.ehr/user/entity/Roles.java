package com.yihu.ehr.user.entity;

import org.springframework.boot.autoconfigure.web.ResourceProperties;

import javax.persistence.*;

/**
 * Created by yww on 2016/7/7.
 */
@Entity
@Table(name = "roles")
@Access(value = AccessType.PROPERTY)
public class Roles {
    private long id;
    private String code;
    private String name;
    private String description;
    private String appId;
    private String type;
    private String orgCode;

    public Roles(){}

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id",unique = true,nullable = false)
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "code",nullable = true)
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "name",nullable = true)
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "description",nullable = true)
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "app_id",nullable = true)
    public String getAppId() {
        return appId;
    }
    public void setAppId(String appId) {
        this.appId = appId;
    }

    @Column(name = "type",nullable = true)
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    @Column(name = "org_code",nullable = true)
    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }
}
