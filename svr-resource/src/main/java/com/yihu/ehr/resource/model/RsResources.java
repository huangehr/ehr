package com.yihu.ehr.resource.model;

import com.yihu.ehr.profile.annotation.*;
import org.apache.hadoop.hbase.shaded.org.codehaus.jackson.annotate.JsonProperty;
import org.apache.hadoop.hbase.shaded.org.codehaus.jackson.annotate.JsonRawValue;
import org.apache.hadoop.hbase.shaded.org.codehaus.jackson.annotate.JsonSetter;
import org.apache.htrace.fasterxml.jackson.annotation.JsonGetter;
import org.apache.htrace.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import javax.persistence.Column;
import javax.persistence.Table;

/**
 * Created by hzp on 2016/4/21.
 * 资源列表
 */
@Entity
@Table(name = "rs_resources")
@Access(value = AccessType.PROPERTY)
public class RsResources {
    private String id;
    private String code;
    private String name;
    private String categoryId;
    private String rsInterface;
    private String grantType;
    private String description;

    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "assigned")
    @Column(name = "id", unique = true, nullable = false)
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    @Column(name="code",nullable = false)
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    @Column(name="name",nullable = false)
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Column(name="category_id",nullable=false)
    public String getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    @Column(name="rs_interface")
    public String getRsInterface()
    {
        return rsInterface;
    }
    public void setRsInterface(String rsInterface)
    {
        this.rsInterface = rsInterface;
    }

    @Column(name="grant_type")
    public String getGrantType()
    {
        return grantType;
    }
    public void setGrantType(String grantType)
    {
        this.grantType = grantType;
    }

    @Column(name="description")
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
