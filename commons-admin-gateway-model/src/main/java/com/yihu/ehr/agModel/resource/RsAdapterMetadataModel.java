package com.yihu.ehr.agModel.resource;

/**
 * Created by lyr on 2016/5/17.
 */
public class RsAdapterMetadataModel {
    private String id;
    private String schemaId;
    private String metadataId;
    private String srcDatasetCode;
    private String srcMetadataCode;
    private String srcMetadataName;
    private String metadataDomainName;
    private String metadataDomain;
    private String metadataName;

    public String getSrcMetadataName() {
        return srcMetadataName;
    }

    public void setSrcMetadataName(String srcMetadataName) {
        this.srcMetadataName = srcMetadataName;
    }

    public String getMetadataDomainName() {
        return metadataDomainName;
    }

    public void setMetadataDomainName(String metadataDomainName) {
        this.metadataDomainName = metadataDomainName;
    }

    public String getMetadataDomain() {
        return metadataDomain;
    }

    public void setMetadataDomain(String metadataDomain) {
        this.metadataDomain = metadataDomain;
    }

    public String getMetadataName() {
        return metadataName;
    }

    public void setMetadataName(String metadataName) {
        this.metadataName = metadataName;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getSchemaId() {
        return schemaId;
    }
    public void setSchemaId(String schemaId) {
        this.schemaId = schemaId;
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
