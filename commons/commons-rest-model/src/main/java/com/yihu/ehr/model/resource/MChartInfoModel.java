package com.yihu.ehr.model.resource;


import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/12.
 */
public class MChartInfoModel {
    private String title; //报表标题
    private String resourceId; //指标ID
    private String resourceCode; //指标Code
    private String option;
    private Map<String,String> dimensionMap;//key: 维度code，value :维度名称

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceCode() {
        return resourceCode;
    }

    public void setResourceCode(String resourceCode) {
        this.resourceCode = resourceCode;
    }

    public Map<String, String> getDimensionMap() {
        return dimensionMap;
    }

    public void setDimensionMap(Map<String, String> dimensionMap) {
        this.dimensionMap = dimensionMap;
    }
}
