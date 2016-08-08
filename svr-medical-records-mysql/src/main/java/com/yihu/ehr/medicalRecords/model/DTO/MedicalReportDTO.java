package com.yihu.ehr.medicalRecords.model.DTO;

import java.sql.Timestamp;

/**
 * Created by hzp on 2016/8/4.
 * 辅助检查报告
 */
public class MedicalReportDTO {

    private int id;
    private String recordId;
    private String reportName;
    private Timestamp reportDatetime;
    private String reportContent;
    private String fileUrlList;  // DID列表,以;分隔

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
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

    public String getFileUrlList() {
        return fileUrlList;
    }

    public void setFileUrlList(String fileUrlList) {
        this.fileUrlList = fileUrlList;
    }
}
