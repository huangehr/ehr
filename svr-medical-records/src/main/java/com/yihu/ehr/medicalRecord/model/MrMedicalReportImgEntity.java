package com.yihu.ehr.medicalRecord.model;

import javax.persistence.*;

/**
 * Created by shine on 2016/7/14.
 */
@Entity
@Table(name = "mr_medical_report_img", schema = "", catalog = "medical_records")
public class MrMedicalReportImgEntity {
    private int id;
    private int reportId;
    private String name;
    private String reportImgUrl;
    private String reportFastdfsImgUrl;
    private int sort;

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
    @Column(name = "REPORT_ID")
    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }


    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Basic
    @Column(name = "REPORT_IMG_URL")
    public String getReportImgUrl() {
        return reportImgUrl;
    }

    public void setReportImgUrl(String reportImgUrl) {
        this.reportImgUrl = reportImgUrl;
    }

    @Basic
    @Column(name = "REPORT_FASTDFS_IMG_URL")
    public String getReportFastdfsImgUrl() {
        return reportFastdfsImgUrl;
    }

    public void setReportFastdfsImgUrl(String reportFastdfsImgUrl) {
        this.reportFastdfsImgUrl = reportFastdfsImgUrl;
    }

    @Basic
    @Column(name = "Sort")
    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MrMedicalReportImgEntity that = (MrMedicalReportImgEntity) o;

        if (id != that.id) return false;
        if (reportId != that.reportId) return false;
        if (sort != that.sort) return false;
        if (reportImgUrl != null ? !reportImgUrl.equals(that.reportImgUrl) : that.reportImgUrl != null) return false;
        if (reportFastdfsImgUrl != null ? !reportFastdfsImgUrl.equals(that.reportFastdfsImgUrl) : that.reportFastdfsImgUrl != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + reportId;
        result = 31 * result + (reportImgUrl != null ? reportImgUrl.hashCode() : 0);
        result = 31 * result + (reportFastdfsImgUrl != null ? reportFastdfsImgUrl.hashCode() : 0);
        result = 31 * result + sort;
        return result;
    }
}
