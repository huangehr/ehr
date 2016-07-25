package com.yihu.ehr.medicalRecord.model;

import java.sql.Timestamp;

/**
 * Created by Guo Yanshan on 2016/7/15.
 */
public class MrMedicalReportInfo {
    private int recordsId;
    private String reportName;
    private Timestamp reportDatetime;

    public int getRecordsId() {
        return recordsId;
    }

    public void setRecordsId(int recordsId) {
        this.recordsId = recordsId;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public Timestamp getReportDatetime() {
        return reportDatetime;
    }

    public void setReportDatetime(Timestamp reportDatetime) {
        this.reportDatetime = reportDatetime;
    }

    public String getReportContent() {
        return reportContent;
    }

    public void setReportContent(String reportContent) {
        this.reportContent = reportContent;
    }

    public String getReportImgUrl() {
        return reportImgUrl;
    }

    public void setReportImgUrl(String reportImgUrl) {
        this.reportImgUrl = reportImgUrl;
    }

    public String getReportFastdfsImgUrl() {
        return reportFastdfsImgUrl;
    }

    public void setReportFastdfsImgUrl(String reportFastdfsImgUrl) {
        this.reportFastdfsImgUrl = reportFastdfsImgUrl;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    private String reportContent;
    private String reportImgUrl;
    private String reportFastdfsImgUrl;
    private int sort;
}
