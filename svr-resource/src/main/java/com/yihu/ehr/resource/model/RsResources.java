package com.yihu.ehr.resource.model;

import org.hibernate.annotations.GenericGenerator;

import javax.annotation.Generated;
import javax.persistence.*;

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
    private String type;
    private String relatedAction;
    private String relatedDatasource;
    private String relatedDatasets;
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

    @Column(name="type",nullable = false)
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    @Column(name="related_action")
    public String getRelatedAction() {
        return relatedAction;
    }
    public void setRelatedAction(String relatedAction) {
        this.relatedAction = relatedAction;
    }

    @Column(name="related_datasource")
    public String getRelatedDatasource() {
        return relatedDatasource;
    }
    public void setRelatedDatasource(String relatedDatasource) {
        this.relatedDatasource = relatedDatasource;
    }

    @Column(name="related_datasets")
    public String getRelatedDatasets() {
        return relatedDatasets;
    }
    public void setRelatedDatasets(String relatedDatasets) {
        this.relatedDatasets = relatedDatasets;
    }

    @Column(name="description")
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
