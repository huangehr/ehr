package com.yihu.ehr.adaption.service;

/**
 * Created by AndyCai on 2015/12/1.
 */
public class DictEntryMappingInfo {
    String id;
    String stdDictEntryId;
    String stdDictEntryCode;
    String stdDictEntryValue;
    String orgDictEntryId;
    String orgDictEntryCode;
    String orgDictEntryValue;
    String adapterDictId;
    String planId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStdDictEntryId() {
        return stdDictEntryId;
    }

    public void setStdDictEntryId(String stdDictEntryId) {
        this.stdDictEntryId = stdDictEntryId;
    }

    public String getStdDictEntryCode() {
        return stdDictEntryCode;
    }

    public void setStdDictEntryCode(String stdDictEntryCode) {
        this.stdDictEntryCode = stdDictEntryCode;
    }

    public String getStdDictEntryValue() {
        return stdDictEntryValue;
    }

    public void setStdDictEntryValue(String stdDictEntryValue) {
        this.stdDictEntryValue = stdDictEntryValue;
    }

    public String getOrgDictEntryId() {
        return orgDictEntryId;
    }

    public void setOrgDictEntryId(String orgDictEntryId) {
        this.orgDictEntryId = orgDictEntryId;
    }

    public String getOrgDictEntryCode() {
        return orgDictEntryCode;
    }

    public void setOrgDictEntryCode(String orgDictEntryCode) {
        this.orgDictEntryCode = orgDictEntryCode;
    }

    public String getOrgDictEntryValue() {
        return orgDictEntryValue;
    }

    public void setOrgDictEntryValue(String orgDictEntryValue) {
        this.orgDictEntryValue = orgDictEntryValue;
    }

    public String getAdapterDictId() {
        return adapterDictId;
    }

    public void setAdapterDictId(String adapterDictId) {
        this.adapterDictId = adapterDictId;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }
}
