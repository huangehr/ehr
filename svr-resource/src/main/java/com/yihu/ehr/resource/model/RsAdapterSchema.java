package com.yihu.ehr.resource.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * 资源适配方案实体
 *
 * Created by lyr on 2016/5/17.
 */
@Entity
@Table(name="rs_adapter_schema")
public class RsAdapterSchema {

    private String id;
    private String type;
    private String name;
    private String code;
    private String adapterVersion;
    private String description;

    @Id
    @GeneratedValue(generator="Generator")
    @GenericGenerator(name="Generator",strategy = "assigned")
    @Column(name="id",nullable = false,unique = true)
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    @Column(name="type",nullable = false)
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    @Column(name="name",nullable = false)
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Column(name="code",nullable = false)
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    @Column(name="adapter_version")
    public String getAdapterVersion() {
        return adapterVersion;
    }
    public void setAdapterVersion(String adapterVersion) {
        this.adapterVersion = adapterVersion;
    }

    @Column(name="description")
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
