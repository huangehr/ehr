package com.yihu.ehr.model.quality;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author yeshijie on 2018/6/11.
 */
public class MDqWarningRecord implements Serializable {

    private String id;
    private String type;//类型(1接收，2资源化，3上传)
    private String warningType;//预警问题类型
    private String orgCode;//机构代码
    private String orgName;//机构名称
    private Date warningTime;//预警时间
    private Date recordTime;//就诊（资源化，上传）时间
    private String status;//状态（1未解决，2已解决）
    private String quota;//指标
    private String actualValue;//实际值
    private String warningValue;//预警值
    private Date solveTime;//解决时间
    private String solveId;//解决人id
    private String solveName;//解决人姓名
    private String solveType;//解决方式（1已解决，2忽略，3无法解决，4不是问题）
    private String problemDescription;//问题描述

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWarningType() {
        return warningType;
    }

    public void setWarningType(String warningType) {
        this.warningType = warningType;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    @JsonFormat(pattern="yyyy-MM-dd", timezone = "GMT+8")
    public Date getWarningTime() {
        return warningTime;
    }

    public void setWarningTime(Date warningTime) {
        this.warningTime = warningTime;
    }

    @JsonFormat(pattern="yyyy-MM-dd", timezone = "GMT+8")
    public Date getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getQuota() {
        return quota;
    }

    public void setQuota(String quota) {
        this.quota = quota;
    }

    public String getActualValue() {
        return actualValue;
    }

    public void setActualValue(String actualValue) {
        this.actualValue = actualValue;
    }

    public String getWarningValue() {
        return warningValue;
    }

    public void setWarningValue(String warningValue) {
        this.warningValue = warningValue;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getSolveTime() {
        return solveTime;
    }

    public void setSolveTime(Date solveTime) {
        this.solveTime = solveTime;
    }

    public String getSolveId() {
        return solveId;
    }

    public void setSolveId(String solveId) {
        this.solveId = solveId;
    }

    public String getSolveName() {
        return solveName;
    }

    public void setSolveName(String solveName) {
        this.solveName = solveName;
    }

    public String getSolveType() {
        return solveType;
    }

    public void setSolveType(String solveType) {
        this.solveType = solveType;
    }

    public String getProblemDescription() {
        return problemDescription;
    }

    public void setProblemDescription(String problemDescription) {
        this.problemDescription = problemDescription;
    }
}
