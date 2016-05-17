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
        appResourceId = appResourceId;
    }

    @Column(name="metadata_id",nullable = false)
    public String getMetadataId() {
        return resourceMetadataId;
    }
    public void setMetadataId(String resourceMetadataId) {
        resourceMetadataId = resourceMetadataId;
    }

    @Column(name="dimension_id")
    public String getDimensionId() {
        return dimensionId;
    }
    public void setDimensionId(String dimensionId) {
        dimensionId = dimensionId;
    }

    @Column(name="dimension_value")
    public String getDimensionValue() {
        return dimensionValue;
    }
    public void setDimensionValue(String dimensionValue) {
        dimensionValue = dimensionValue;
    }

}
