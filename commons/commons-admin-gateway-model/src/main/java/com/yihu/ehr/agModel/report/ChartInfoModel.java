package com.yihu.ehr.agModel.report;


import java.util.Map;

/**
 * Created by Administrator on 2017/8/12.
 */
public class ChartInfoModel {
    private String title; //报表标题
    private String resourceId; //视图ID
    private String resourceCode; //视图code
    private String option;
    private Map<String,Object> dimensionMap;//key: 维度code，value :维度名称

    private Map<String,String> xAxisMap;//key:X轴名称，value : X轴数据的code
    private String firstDimension;

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

    public Map<String, Object> getDimensionMap() {
        return dimensionMap;
    }

    public void setDimensionMap(Map<String, Object> dimensionMap) {
        this.dimensionMap = dimensionMap;
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

    public String getFirstDimension() {
        return firstDimension;
    }

    public void setFirstDimension(String firstDimension) {
        this.firstDimension = firstDimension;
    }

    public Map<String, String> getxAxisMap() {
        return xAxisMap;
    }

    public void setxAxisMap(Map<String, String> xAxisMap) {
        this.xAxisMap = xAxisMap;
    }
}
