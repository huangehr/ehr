package com.yihu.ehr.resource.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * 适配数据元
 *
 * Created by lyr on 2016/5/17.
 */
@Entity
@Table(name="rs_adapter_metadata")
public class RsAdapterMetadata {
    private String id;
    private String schemaId;
    private String metadataId;
    private String srcDatasetCode;
    private String srcMetadataCode;

    @Id
    @GeneratedValue(generator="Generator")
    @GenericGenerator(name="Generator",strategy = "assigned")
    @Column(name="id",nullable = false,unique = true)
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    @Column(name="schema_id",nullable = false)
    public String getSchemaId() {
        return schemaId;
    }
    public void setSchemaId(String schemaId) {
        this.schemaId = schemaId;
    }

    @Column(name="metadata_id",nullable = false)
    public String getMetadataId() {
        return metadataId;
    }
    public void setMetadataId(String metadataId) {
        this.metadataId = metadataId;
    }

    @Column(name="src_dataset_code",nullable = false)
    public String getSrcDatasetCode() {
        return srcDatasetCode;
    }
    public void setSrcDatasetCode(String srcDatasetCode) {
        this.srcDatasetCode = srcDatasetCode;
    }

    @Column(name="src_metadata_code")
    public String getSrcMetadataCode() {
        return srcMetadataCode;
    }
    public void setSrcMetadataCode(String srcMetadataCode) {
        this.srcMetadataCode = srcMetadataCode;
    }
}
