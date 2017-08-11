package com.yihu.ehr.agModel.resource;

/**
 * Created by Administrator on 2017/8/10.
 */
public class ResourceQuotaModel {
    private int id;
    private String resourceId;
    private String quotaTypeName;
    private String quotaChart;
    private int quotaId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getQuotaTypeName() {
        return quotaTypeName;
    }

    public void setQuotaTypeName(String quotaTypeName) {
        this.quotaTypeName = quotaTypeName;
    }

    public String getQuotaChart() {
        return quotaChart;
    }

    public void setQuotaChart(String quotaChart) {
        this.quotaChart = quotaChart;
    }

    public int getQuotaId() {
        return quotaId;
    }

    public void setQuotaId(int quotaId) {
        this.quotaId = quotaId;
    }
}
