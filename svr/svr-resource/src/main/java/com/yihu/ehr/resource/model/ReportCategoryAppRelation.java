package com.yihu.ehr.resource.model;

import javax.persistence.*;

/**
 * Created by wxw on 2017/11/24.
 */
@Entity
@Table(name = "report_category_app_relation")
public class ReportCategoryAppRelation {
    private int id;
    private int reportCategoryId;
    private String appId;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "report_category_id")
    public int getReportCategoryId() {
        return reportCategoryId;
    }

    public void setReportCategoryId(int reportCategoryId) {
        this.reportCategoryId = reportCategoryId;
    }

    @Column(name = "app_id")
    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
}
