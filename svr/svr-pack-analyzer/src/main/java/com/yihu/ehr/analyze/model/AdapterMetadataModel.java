package com.yihu.ehr.analyze.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by lingfeng on 2015/9/16.
 */
public class AdapterMetadataModel implements Serializable {
    @Id
    @GeneratedValue(generator = "generator")
    @GenericGenerator(name = "generator", strategy = "increment")
    @Column(name = "id")
    private Long id;
    @Column(name = "scheme_id")
    private Long schemeId;
    @Column(name = "std_dataset_id")
    private Long stdDatasetId;
    @Column(name = "std_metadata_id")
    private Long stdMetadataId;
    @Column(name = "std_metadata_code")
    private String stdMetadataCode;
    @Column(name = "std_metadata_name")
    private String stdMetadataName;
    @Column(name = "std_dict_id")
    private Long stdDictId;
    @Column(name = "adapter_dataset_id")
    private Long adapterDatasetId;
    @Column(name = "adapter_metadata_id")
    private Long adapterMetadataId;
    @Column(name = "adapter_metadata_code")
    private String adapterMetadataCode;
    @Column(name = "adapter_metadata_name")
    private String adapterMetadataName;
    @Column(name = "adapter_data_type")
    private Integer adapterDataType;
    @Column(name = "adapter_dict_id")
    private Long adapterDictId;
    @Column(name = "adapter_info")
    private String adapterInfo;
    @Column(name = "belong_adapter_id")
    private Long belongAdapterId;
    @Column(name = "adapter_default")
    private String adapterDefault;
    @Column(name="need_crawer")
    private Integer needCrawer;//0不需要采集   1需要采集(默认)

    public String getAdapterInfo() {
        return adapterInfo;
    }

    public void setAdapterInfo(String adapterInfo) {
        this.adapterInfo = adapterInfo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(Long schemeId) {
        this.schemeId = schemeId;
    }

    public Long getStdDatasetId() {
        return stdDatasetId;
    }

    public void setStdDatasetId(Long stdDatasetId) {
        this.stdDatasetId = stdDatasetId;
    }

    public Long getStdMetadataId() {
        return stdMetadataId;
    }

    public void setStdMetadataId(Long stdMetadataId) {
        this.stdMetadataId = stdMetadataId;
    }

    public String getStdMetadataCode() {
        return stdMetadataCode;
    }

    public void setStdMetadataCode(String stdMetadataCode) {
        this.stdMetadataCode = stdMetadataCode;
    }

    public String getStdMetadataName() {
        return stdMetadataName;
    }

    public void setStdMetadataName(String stdMetadataName) {
        this.stdMetadataName = stdMetadataName;
    }

    public Long getStdDictId() {
        return stdDictId;
    }

    public void setStdDictId(Long stdDictId) {
        this.stdDictId = stdDictId;
    }

    public Long getAdapterDatasetId() {
        return adapterDatasetId;
    }

    public void setAdapterDatasetId(Long adapterDatasetId) {
        this.adapterDatasetId = adapterDatasetId;
    }

    public Long getAdapterMetadataId() {
        return adapterMetadataId;
    }

    public void setAdapterMetadataId(Long adapterMetadataId) {
        this.adapterMetadataId = adapterMetadataId;
    }

    public String getAdapterMetadataCode() {
        return adapterMetadataCode;
    }

    public void setAdapterMetadataCode(String adapterMetadataCode) {
        this.adapterMetadataCode = adapterMetadataCode;
    }

    public String getAdapterMetadataName() {
        return adapterMetadataName;
    }

    public void setAdapterMetadataName(String adapterMetadataName) {
        this.adapterMetadataName = adapterMetadataName;
    }

    public Integer getAdapterDataType() {
        return adapterDataType;
    }

    public void setAdapterDataType(Integer adapterDataType) {
        this.adapterDataType = adapterDataType;
    }

    public Long getAdapterDictId() {
        return adapterDictId;
    }

    public void setAdapterDictId(Long adapterDictId) {
        this.adapterDictId = adapterDictId;
    }

    public Long getBelongAdapterId() {
        return belongAdapterId;
    }

    public void setBelongAdapterId(Long belongAdapterId) {
        this.belongAdapterId = belongAdapterId;
    }

    public String getAdapterDefault() {
        return adapterDefault;
    }

    public void setAdapterDefault(String adapterDefault) {
        this.adapterDefault = adapterDefault;
    }

    public Integer getNeedCrawer() {
        return needCrawer;
    }

    public void setNeedCrawer(Integer needCrawer) {
        this.needCrawer = needCrawer;
    }
}
