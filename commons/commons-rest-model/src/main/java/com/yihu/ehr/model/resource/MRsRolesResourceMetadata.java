package com.yihu.ehr.model.resource;

/**
 * Created by lyr on 2016/5/4.
 */
public class MRsRolesResourceMetadata {
    private String id;
    private String rolesResourceId;
    private String rolesId;
    private String resourceMetadataId;
    private String resourceMetadataName;
    private String dimensionId;
    private String dimensionValue;
    private String valid = "1";

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getRolesResourceId() {
        return rolesResourceId;
    }

    public void setRolesResourceId(String rolesResourceId) {
        this.rolesResourceId = rolesResourceId;
    }

    public String getRolesId() {
        return rolesId;
    }

    public void setRolesId(String rolesId) {
        this.rolesId = rolesId;
    }

    public String getResourceMetadataId() {
        return resourceMetadataId;
    }
    public void setResourceMetadataId(String resourceMetadataId) {
        this.resourceMetadataId = resourceMetadataId;
    }

    public String getDimensionId() {
        return dimensionId;
    }
    public void setDimensionId(String dimensionId) {
        this.dimensionId = dimensionId;
    }

    public String getDimensionValue() {
        return dimensionValue;
    }
    public void setDimensionValue(String dimensionValue) {
        this.dimensionValue = dimensionValue;
    }

    public String getResourceMetadataName() {
        return resourceMetadataName;
    }

    public void setResourceMetadataName(String resourceMetadataName) {
        this.resourceMetadataName = resourceMetadataName;
    }

    public String getValid() {
        return valid;
    }

    public void setValid(String valid) {
        this.valid = valid;
    }
}
