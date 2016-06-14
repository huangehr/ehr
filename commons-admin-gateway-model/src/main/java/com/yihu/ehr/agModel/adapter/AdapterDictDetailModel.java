package com.yihu.ehr.agModel.adapter;

/**
 * Created by AndyCai on 2016/3/15.
 */
public class AdapterDictDetailModel {

    private Long id;

    private Long adapterPlanId;

    /**
     * 所属标准字典
     */
    private Long dictId;
    /**
     * 标准字典项
     */
    private Long dictEntryId;

    /**
     * 所属机构字典
     */
    private Long orgDictSeq;
    /**
     * 机构字典项
     */
    private Long orgDictEntrySeq;
    /**
     * 说明
     */
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAdapterPlanId() {
        return adapterPlanId;
    }

    public void setAdapterPlanId(Long adapterPlanId) {
        this.adapterPlanId = adapterPlanId;
    }

    public Long getDictId() {
        return dictId;
    }

    public void setDictId(Long dictId) {
        this.dictId = dictId;
    }

    public Long getDictEntryId() {
        return dictEntryId;
    }

    public void setDictEntryId(Long dictEntryId) {
        this.dictEntryId = dictEntryId;
    }

    public Long getOrgDictSeq() {
        return orgDictSeq;
    }

    public void setOrgDictSeq(Long orgDictSeq) {
        this.orgDictSeq = orgDictSeq;
    }

    public Long getOrgDictEntrySeq() {
        return orgDictEntrySeq;
    }

    public void setOrgDictEntrySeq(Long orgDictEntrySeq) {
        this.orgDictEntrySeq = orgDictEntrySeq;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
