package com.yihu.ehr.model.report;

import java.util.Date;

/**
 * Created by janseny on 2017/5/8.
 */
public class MQcDailyReportDetail {

    private String id;
    private String reportId;
    private String archiveType;//档案分类  outpatient - 门诊    hospital - 住院
    private String patientId;//病人id
    private String eventNo;//事件号
    private Date eventTime;//事件时间
    private Integer acqFlag;//采集状态  0 - 未采集  1 - 已采集（实收） 验证统计用
    private Integer timelyFlag;//及时状态 0 - 不及时  1 -及时，判断条件：采集时间 - 事件时间 > 2天 判断为不及时  不及时条件可以配置定义
    private Date addDate;
    private Date acqTime;   //  采集时间
    private Date storageTime;  //入库时间
    private Integer storageFlag;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getArchiveType() {
        return archiveType;
    }

    public void setArchiveType(String archiveType) {
        this.archiveType = archiveType;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getEventNo() {
        return eventNo;
    }

    public void setEventNo(String eventNo) {
        this.eventNo = eventNo;
    }

    public Date getEventTime() {
        return eventTime;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    public Integer getAcqFlag() {
        return acqFlag;
    }

    public void setAcqFlag(Integer acqFlag) {
        this.acqFlag = acqFlag;
    }

    public Integer getTimelyFlag() {
        return timelyFlag;
    }

    public void setTimelyFlag(Integer timelyFlag) {
        this.timelyFlag = timelyFlag;
    }

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }

    public Date getAcqTime() {
        return acqTime;
    }

    public void setAcqTime(Date acqTime) {
        this.acqTime = acqTime;
    }

    public Date getStorageTime() {
        return storageTime;
    }

    public void setStorageTime(Date storageTime) {
        this.storageTime = storageTime;
    }

    public Integer getStorageFlag() {
        return storageFlag;
    }

    public void setStorageFlag(Integer storageFlag) {
        this.storageFlag = storageFlag;
    }
}
