package com.yihu.ehr.agModel.org;

import java.util.List;

/**
 * Created by zdm on 2017/6/15
 */
public class RsOrgResourceMetadataModel {
    private String id;
    private String organizationResourceId;
    private String organizationId;
    private String resourceMetadataId;
    private String resourceMetadataName;
    private String dimensionId;
    private String dimensionValue;
    private String valid = "1";
    private List dictEntries;
    private String metaColunmType;
    private String metaId;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getOrganizationResourceId() {
        return organizationResourceId;
    }

    public void setOrganizationResourceId(String organizationResourceId) {
        this.organizationResourceId = organizationResourceId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
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

    public List getDictEntries() {
        return dictEntries;
    }

    public void setDictEntries(List dictEntries) {
        this.dictEntries = dictEntries;
    }

    public String getMetaColunmType() {
        return metaColunmType;
    }

    public void setMetaColunmType(String metaColunmType) {
        this.metaColunmType = metaColunmType;
    }

    public String getMetaId() {
        return metaId;
    }

    public void setMetaId(String metaId) {
        this.metaId = metaId;
    }
}
