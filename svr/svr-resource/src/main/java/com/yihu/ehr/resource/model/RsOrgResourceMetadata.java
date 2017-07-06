package com.yihu.ehr.resource.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by zdm on 2017/6/15
 */
@Entity
@Table(name="rs_organization_resource_metadata")
public class RsOrgResourceMetadata {
    private String id;
    private String organizationId;
    private String organizationResourceId;
    private String resourceMetadataId;
    private String resourceMetadataName;
    private String dimensionId;
    private String dimensionValue;
    private String valid = "1";

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
    @Column(name="organization_id",nullable = false)
    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }
    @Column(name="organization_resource_id",nullable = false)
    public String getOrganizationResourceId() {
        return organizationResourceId;
    }

    public void setOrganizationResourceId(String organizationResourceId) {
        this.organizationResourceId = organizationResourceId;
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

    @Column(name="resource_metadata_name",nullable = false)
    public String getResourceMetadataName() {
        return resourceMetadataName;
    }
    public void setResourceMetadataName(String resourceMetadataName) {
        this.resourceMetadataName = resourceMetadataName;
    }

    @Column(name="valid",nullable = false)
    public String getValid() {
        return valid;
    }
    public void setValid(String valid) {
        this.valid = valid;
    }
}
