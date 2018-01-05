package com.yihu.ehr.adaption.model;

/**
 * Created by AndyCai on 2015/11/30.
 */
public class MetadataMappingInfo {
    String id;
    String stdMetadataId;
    String orgMetadataId;
    String stdMetadataCode;
    String orgMetadataCode;
    String stdMetadataName;
    String orgMetadataName;
    String adapterDataSetId;
    String orgDictDataType;
    String planId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStdMetadataId() {
        return stdMetadataId;
    }

    public void setStdMetadataId(String stdMetadataId) {
        this.stdMetadataId = stdMetadataId;
    }

    public String getOrgMetadataId() {
        return orgMetadataId;
    }

    public void setOrgMetadataId(String orgMetadataId) {
        this.orgMetadataId = orgMetadataId;
    }

    public String getStdMetadataCode() {
        return stdMetadataCode;
    }

    public void setStdMetadataCode(String stdMetadataCode) {
        this.stdMetadataCode = stdMetadataCode;
    }

    public String getOrgMetadataCode() {
        return orgMetadataCode;
    }

    public void setOrgMetadataCode(String orgMetadataCode) {
        this.orgMetadataCode = orgMetadataCode;
    }

    public String getStdMetadataName() {
        return stdMetadataName;
    }

    public void setStdMetadataName(String stdMetadataName) {
        this.stdMetadataName = stdMetadataName;
    }

    public String getOrgMetadataName() {
        return orgMetadataName;
    }

    public void setOrgMetadataName(String orgMetadataName) {
        this.orgMetadataName = orgMetadataName;
    }

    public String getAdapterDataSetId() {
        return adapterDataSetId;
    }

    public void setAdapterDataSetId(String adapterDataSetId) {
        this.adapterDataSetId = adapterDataSetId;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getOrgDictDataType() {
        return orgDictDataType;
    }

    public void setOrgDictDataType(String orgDictDataType) {
        this.orgDictDataType = orgDictDataType;
    }
}
