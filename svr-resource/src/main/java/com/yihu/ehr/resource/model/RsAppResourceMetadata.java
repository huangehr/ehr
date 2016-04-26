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
    private String AppResourceId;
    private String MetadataId;
    private String DimensionId;
    private String DimensionValue;

    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name="Generator",strategy = "assigned")
    @Column(name="id",unique = true,nullable = false)
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    @Column(name="app_resource_id",nullable = false)
    public String getAppResourceId() {
        return AppResourceId;
    }
    public void setAppResourceId(String appResourceId) {
        AppResourceId = appResourceId;
    }

    @Column(name="metadata_id",nullable = false)
    public String getMetadataId() {
        return MetadataId;
    }
    public void setMetadataId(String metadataId) {
        MetadataId = metadataId;
    }

    @Column(name="dimension_id")
    public String getDimensionId() {
        return DimensionId;
    }
    public void setDimensionId(String dimensionId) {
        DimensionId = dimensionId;
    }

    @Column(name="dimension_value")
    public String getDimensionValue() {
        return DimensionValue;
    }
    public void setDimensionValue(String dimensionValue) {
        DimensionValue = dimensionValue;
    }

}
