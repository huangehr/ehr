package com.yihu.ehr.analyze.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by lingfeng on 2015/9/16.
 */
public class AdapterDatasetModel  implements Serializable {
    @Id
    @GeneratedValue(generator = "generator")
    @GenericGenerator(name = "generator", strategy = "increment")
    @Column(name = "id")
    private Long id;
    @Column(name = "scheme_id")
    private Long schemeId;
    @Column(name = "std_dataset_id")
    private Long stdDatasetId;
    @Column(name = "std_dataset_code")
    private String stdDatasetCode;
    @Column(name = "std_dataset_name")
    private String stdDatasetName;
    @Column(name = "adapter_dataset_id")
    private String adapterDatasetId;
    @Column(name = "adapter_dataset_code")
    private String adapterDatasetCode;
    @Column(name = "adapter_dataset_name")
    private String adapterDatasetName;
    @Column(name="main_dataset_code")
    private String mainDatasetCode;
    @Column(name="main_dataset_name")
    private String mainDatasetName;
    @Column(name="is_clone")
    private Integer isClone; //0 不是克隆(默认)   1是克隆
    @Column(name="need_crawer")
    private Integer needCrawer;//0不需要采集   1需要采集(默认)


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

    public String getStdDatasetCode() {
        return stdDatasetCode;
    }

    public void setStdDatasetCode(String stdDatasetCode) {
        this.stdDatasetCode = stdDatasetCode;
    }

    public String getStdDatasetName() {
        return stdDatasetName;
    }

    public void setStdDatasetName(String stdDatasetName) {
        this.stdDatasetName = stdDatasetName;
    }

    public String getAdapterDatasetId() {
        return adapterDatasetId;
    }

    public void setAdapterDatasetId(String adapterDatasetId) {
        this.adapterDatasetId = adapterDatasetId;
    }

    public String getAdapterDatasetCode() {
        return adapterDatasetCode;
    }

    public void setAdapterDatasetCode(String adapterDatasetCode) {
        this.adapterDatasetCode = adapterDatasetCode;
    }

    public String getAdapterDatasetName() {
        return adapterDatasetName;
    }

    public void setAdapterDatasetName(String adapterDatasetName) {
        this.adapterDatasetName = adapterDatasetName;
    }

    public String getMainDatasetCode() {
        return mainDatasetCode;
    }

    public void setMainDatasetCode(String mainDatasetCode) {
        this.mainDatasetCode = mainDatasetCode;
    }

    public String getMainDatasetName() {
        return mainDatasetName;
    }

    public void setMainDatasetName(String mainDatasetName) {
        this.mainDatasetName = mainDatasetName;
    }


    public Integer getIsClone() {
        return isClone;
    }

    public void setIsClone(Integer isClone) {
        this.isClone = isClone;
    }

    public Integer getNeedCrawer() {
        return needCrawer;
    }

    public void setNeedCrawer(Integer needCrawer) {
        this.needCrawer = needCrawer;
    }
}
