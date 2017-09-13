package com.yihu.ehr.model.resource;

/**
 * Created by Administrator on 2017/8/10.
 */
public class MResourceQuota {
    private int id;
    private String resourceId;
    private String quotaTypeName;
    private int quotaChart;  ////已选择的图表类型
    private int quotaId;
    private String quotaName;
    private String chartType;   ///该指标可配置的图表类型

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

    public int getQuotaChart() {
        return quotaChart;
    }

    public void setQuotaChart(int quotaChart) {
        this.quotaChart = quotaChart;
    }

    public int getQuotaId() {
        return quotaId;
    }

    public void setQuotaId(int quotaId) {
        this.quotaId = quotaId;
    }

    public String getQuotaName() {
        return quotaName;
    }

    public void setQuotaName(String quotaName) {
        this.quotaName = quotaName;
    }

    public String getChartType() {
        return chartType;
    }

    public void setChartType(String chartType) {
        this.chartType = chartType;
    }
}
