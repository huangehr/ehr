package com.yihu.ehr.resource.model;

import javax.persistence.*;

/**
 * Created by Administrator on 2017/8/10.
 */
@Entity
@Table(name="rs_resources_quota")
public class ResourceQuota {
    private int id;
    private String resourceId;
    private String quotaTypeName;
    private String quotaChart;
    private int quotaId;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "resource_id")
    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    @Column(name = "quota_type_name")
    public String getQuotaTypeName() {
        return quotaTypeName;
    }

    public void setQuotaTypeName(String quotaTypeName) {
        this.quotaTypeName = quotaTypeName;
    }

    @Column(name = "quota_chart")
    public String getQuotaChart() {
        return quotaChart;
    }

    public void setQuotaChart(String quotaChart) {
        this.quotaChart = quotaChart;
    }

    @Column(name = "quota_id")
    public int getQuotaId() {
        return quotaId;
    }

    public void setQuotaId(int quotaId) {
        this.quotaId = quotaId;
    }
}
