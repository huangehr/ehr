package com.yihu.ehr.adaption.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * 方案字典映射
 *
 * @author lincl
 * @version 1.0
 * @created 2016.2.3
 */
@Entity
@Table(name = "adapter_dict")
public class AdapterDict {


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

    @Column(name = "plan_id", unique = false, nullable = false)
    public Long getAdapterPlanId() {
        return adapterPlanId;
    }

    public void setAdapterPlanId(Long adapterPlanId) {
        this.adapterPlanId = adapterPlanId;
    }

    @Column(name = "std_dict", unique = false, nullable = false)
    public Long getDictId() {
        return dictId;
    }

    public void setDictId(Long dictId) {
        this.dictId = dictId;
    }

    @Column(name = "std_dictentry", unique = false, nullable = false)
    public Long getDictEntryId() {
        return dictEntryId;
    }

    public void setDictEntryId(Long dictEntryId) {
        this.dictEntryId = dictEntryId;
    }

    @Column(name = "org_dict", unique = false, nullable = true)
    public Long getOrgDictSeq() {
        return orgDictSeq;
    }

    public void setOrgDictSeq(Long orgDictSeq) {
        this.orgDictSeq = orgDictSeq;
    }

    @Column(name = "org_dictentry", unique = false, nullable = true)
    public Long getOrgDictEntrySeq() {
        return orgDictEntrySeq;
    }

    public void setOrgDictEntrySeq(Long orgDictEntrySeq) {
        this.orgDictEntrySeq = orgDictEntrySeq;
    }

    @Column(name = "description", unique = false, nullable = true)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}