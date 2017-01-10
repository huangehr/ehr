package com.yihu.ehr.adaption.dataset.service;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.1
 */
@Entity
@Table(name = "adapter_dataset")
public class AdapterDataSet {

    private Long id;
    /**
     * 所属方案
     */
    private Long adapterPlanId;
    /**
     * 所属标准数据集
     */
    private Long dataSetId;
    /**
     * 标准数据元
     */
    private Long metaDataId;
    /**
     * 数据类型
     */
    private String dataType;
    /**
     * 所属机构数据集
     */
    private Long orgDataSetSeq;
    /**
     * 机构数据元
     */
    private Long orgMetaDataSeq;

    private String description;

    private Long stdDict;

    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "increment")
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "std_dict", unique = false, nullable = true)
    public Long getStdDict() {
        return stdDict;
    }

    public void setStdDict(Long stdDict) {
        this.stdDict = stdDict;
    }

    @Column(name = "description", unique = false, nullable = true)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "org_metadata", unique = false, nullable = true)
    public Long getOrgMetaDataSeq() {
        return orgMetaDataSeq;
    }

    public void setOrgMetaDataSeq(Long orgMetaDataSeq) {
        this.orgMetaDataSeq = orgMetaDataSeq;
    }

    @Column(name = "org_dataset", unique = false, nullable = true)
    public Long getOrgDataSetSeq() {
        return orgDataSetSeq;
    }

    public void setOrgDataSetSeq(Long orgDataSetSeq) {
        this.orgDataSetSeq = orgDataSetSeq;
    }

    @Column(name = "data_type", unique = false, nullable = true)
    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    @Column(name = "std_metadata", unique = false, nullable = true)
    public Long getMetaDataId() {
        return metaDataId;
    }

    public void setMetaDataId(Long metaDataId) {
        this.metaDataId = metaDataId;
    }

    @Column(name = "std_dataset", unique = false, nullable = false)
    public Long getDataSetId() {
        return dataSetId;
    }

    public void setDataSetId(Long dataSetId) {
        this.dataSetId = dataSetId;
    }

    @Column(name = "plan_id", unique = false, nullable = false)
    public Long getAdapterPlanId() {
        return adapterPlanId;
    }

    public void setAdapterPlanId(Long adapterPlanId) {
        this.adapterPlanId = adapterPlanId;
    }


}