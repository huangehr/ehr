package com.yihu.ehr.medicalRecords.model.DTO;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

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
    private String fileIds;  // ID列表,以,分隔
    private List<Map> fileMaps;  // ID列表,以,分隔

    public List<Map> getFileMaps() {
        return fileMaps;
    }

    public void setFileMaps(List<Map> fileMaps) {
        this.fileMaps = fileMaps;
    }

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

    public String getFileIds() {
        return fileIds;
    }

    public void setFileIds(String fileIds) {
        this.fileIds = fileIds;
    }
}
