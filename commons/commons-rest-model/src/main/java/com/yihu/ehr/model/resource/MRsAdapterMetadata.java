package com.yihu.ehr.model.resource;

/**
 * Created by lyr on 2016/5/17.
 */
public class MRsAdapterMetadata {
    private String id;
    private String schemeId;
    private String metadataId;
    private String srcDatasetCode;
    private String srcMetadataCode;
    private String srcMetadataName;
    private String metadataDomain;

    public String getSrcMetadataName() {
        return srcMetadataName;
    }

    public void setSrcMetadataName(String srcMetadataName) {
        this.srcMetadataName = srcMetadataName;
    }

    public String getMetadataDomain() {
        return metadataDomain;
    }

    public void setMetadataDomain(String metadataDomain) {
        this.metadataDomain = metadataDomain;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(String schemeId) {
        this.schemeId = schemeId;
    }

    public String getMetadataId() {
        return metadataId;
    }
    public void setMetadataId(String metadataId) {
        this.metadataId = metadataId;
    }

    public String getSrcDatasetCode() {
        return srcDatasetCode;
    }
    public void setSrcDatasetCode(String srcDatasetCode) {
        this.srcDatasetCode = srcDatasetCode;
    }

    public String getSrcMetadataCode() {
        return srcMetadataCode;
    }
    public void setSrcMetadataCode(String srcMetadataCode) {
        this.srcMetadataCode = srcMetadataCode;
    }
}
