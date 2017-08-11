package com.yihu.ehr.model.tj;

import java.util.List;

/**
 * Created by Administrator on 2017/8/10.
 */
public class MQuotaConfigModel {
    private String quotaTypeName;   //指标分类
    private String quotaName;   //指标名称
    private String quotaCode;   //指标编码
    private String chartType; //图表
    private boolean flag;
    private Integer quotaId;    //指标编号
    private String quotaChart;  //已选中那个图标

    public String getQuotaTypeName() {
        return quotaTypeName;
    }

    public void setQuotaTypeName(String quotaTypeName) {
        this.quotaTypeName = quotaTypeName;
    }

    public String getQuotaName() {
        return quotaName;
    }

    public void setQuotaName(String quotaName) {
        this.quotaName = quotaName;
    }

    public String getQuotaCode() {
        return quotaCode;
    }

    public void setQuotaCode(String quotaCode) {
        this.quotaCode = quotaCode;
    }

    public String getChartType() {
        return chartType;
    }

    public void setChartType(String chartType) {
        this.chartType = chartType;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public Integer getQuotaId() {
        return quotaId;
    }

    public void setQuotaId(Integer quotaId) {
        this.quotaId = quotaId;
    }

    public String getQuotaChart() {
        return quotaChart;
    }

    public void setQuotaChart(String quotaChart) {
        this.quotaChart = quotaChart;
    }
}
