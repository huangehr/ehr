package com.yihu.ehr.model.resource;


import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/12.
 */
public class MChartInfoModel {
    private String title; //报表标题
    private String quotaId; //指标ID
    private String quotaCode; //指标Code
    private String option;
    private List<MReportDimension> listMap;
    private Map<String,Object> dimensionMap;//维度真实值的 code ,中文说明 对应关系

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuotaId() {
        return quotaId;
    }

    public void setQuotaId(String quotaId) {
        this.quotaId = quotaId;
    }

    public String getQuotaCode() {
        return quotaCode;
    }

    public void setQuotaCode(String quotaCode) {
        this.quotaCode = quotaCode;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public List<MReportDimension> getListMap() {
        return listMap;
    }

    public void setListMap(List<MReportDimension> listMap) {
        this.listMap = listMap;
    }

    public Map<String, Object> getDimensionMap() {
        return dimensionMap;
    }

    public void setDimensionMap(Map<String, Object> dimensionMap) {
        this.dimensionMap = dimensionMap;
    }
}
