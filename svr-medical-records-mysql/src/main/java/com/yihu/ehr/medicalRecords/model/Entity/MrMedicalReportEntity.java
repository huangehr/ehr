package com.yihu.ehr.medicalRecords.model.Entity;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by hzp on 2016/8/4.
 */
@Entity
@Table(name = "mr_medical_report", schema = "", catalog = "")
public class MrMedicalReportEntity {
    private int id;
    private String recordId;
    private String reportName;
    private Timestamp reportDatetime;
    private String reportContent;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "ID")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "RECORD_ID")
    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    @Basic
    @Column(name = "REPORT_NAME")
    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    @Basic
    @Column(name = "REPORT_DATETIME")
    public Timestamp getReportDatetime() {
        return reportDatetime;
    }

    public void setReportDatetime(Timestamp reportDatetime) {
        this.reportDatetime = reportDatetime;
    }

    @Basic
    @Column(name = "REPORT_CONTENT")
    public String getReportContent() {
        return reportContent;
    }

    public void setReportContent(String reportContent) {
        this.reportContent = reportContent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MrMedicalReportEntity that = (MrMedicalReportEntity) o;

        if (id != that.id) return false;
        if (recordId != that.recordId) return false;
        if (reportName != null ? !reportName.equals(that.reportName) : that.reportName != null) return false;
        if (reportDatetime != null ? !reportDatetime.equals(that.reportDatetime) : that.reportDatetime != null)
            return false;
        if (reportContent != null ? !reportContent.equals(that.reportContent) : that.reportContent != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (recordId != null ? recordId.hashCode() : 0);
        result = 31 * result + (reportName != null ? reportName.hashCode() : 0);
        result = 31 * result + (reportDatetime != null ? reportDatetime.hashCode() : 0);
        result = 31 * result + (reportContent != null ? reportContent.hashCode() : 0);
        return result;
    }
}
