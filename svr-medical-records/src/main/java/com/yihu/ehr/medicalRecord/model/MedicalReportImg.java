package com.yihu.ehr.medicalRecord.model;

/**
 * Created by shine on 2016/7/14.
 */
public class MedicalReportImg {
    private String name;
    private String reportImgUrl;
    private String reportFastdfsImgUrl;
    private int sort;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
