package com.yihu.ehr.resource.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by lyr on 2016/4/26.
 */
@Entity
@Table(name="rs_app_resource_metadata")
public class RsAppResourceMetadata {
    private String id;
    private String appId;
    private String appResourceId;
    private String resourceMetadataId;
    private String dimensionId;
    private String dimensionValue;

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

    @Column(name="app_resource_id",nullable = false)
    public String getAppResourceId() {
        return appResourceId;
    }
    public void setAppResourceId(String appResourceId) {
        this.appResourceId = appResourceId;
    }

    @Column(name="app_id",nullable = false)
    public String getAppId() {
        return appId;
    }
    public void setAppId(String appId) {
        this.appId = appId;
    }

    @Column(name="resource_metadata_id",nullable = false)
    public String getResourceMetadataId() {
        return resourceMetadataId;
    }
    public void setResourceMetadataId(String resourceMetadataId) {
        this.resourceMetadataId = resourceMetadataId;
    }

    @Column(name="dimension_id")
    public String getDimensionId() {
        return dimensionId;
    }
    public void setDimensionId(String dimensionId) {
        this.dimensionId = dimensionId;
    }

    @Column(name="dimension_value")
    public String getDimensionValue() {
        return dimensionValue;
    }
    public void setDimensionValue(String dimensionValue) {
        this.dimensionValue = dimensionValue;
    }

}
