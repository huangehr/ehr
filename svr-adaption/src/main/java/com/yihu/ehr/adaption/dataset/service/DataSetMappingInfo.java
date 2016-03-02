package com.yihu.ehr.adaption.dataset.service;

/**
 * Created by AndyCai on 2015/11/30.
 */
public class DataSetMappingInfo {

    String id;
    String stdSetId;
    String orgSetId;
    String stdSetCode;
    String orgSetCode;
    String stdSetName;
    String orgSetName;
    String planId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStdSetId() {
        return stdSetId;
    }

    public void setStdSetId(String stdSetId) {
        this.stdSetId = stdSetId;
    }

    public String getOrgSetId() {
        return orgSetId;
    }

    public void setOrgSetId(String orgSetId) {
        this.orgSetId = orgSetId;
    }

    public String getStdSetCode() {
        return stdSetCode;
    }

    public void setStdSetCode(String stdSetCode) {
        this.stdSetCode = stdSetCode;
    }

    public String getOrgSetCode() {
        return orgSetCode;
    }

    public void setOrgSetCode(String orgSetCode) {
        this.orgSetCode = orgSetCode;
    }

    public String getStdSetName() {
        return stdSetName;
    }

    public void setStdSetName(String stdSetName) {
        this.stdSetName = stdSetName;
    }

    public String getOrgSetName() {
        return orgSetName;
    }

    public void setOrgSetName(String orgSetName) {
        this.orgSetName = orgSetName;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }
}
