package com.yihu.ehr.model.tj;

import java.util.List;
import java.util.Map;

/**
 * Created by janseny
 */
public class EchartReportModel {
    private String name;//名称
    private String[] xData;//柱状和线性图数据集
    private Integer[] yData;//柱状和线性图数据集
    private List<MapDataModel> dataModels;//饼状图数据集

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getxData() {
        return xData;
    }

    public void setxData(String[] xData) {
        this.xData = xData;
    }

    public Integer[] getyData() {
        return yData;
    }

    public void setyData(Integer[] yData) {
        this.yData = yData;
    }

    public List<MapDataModel> getDataModels() {
        return dataModels;
    }

    public void setDataModels(List<MapDataModel> dataModels) {
        this.dataModels = dataModels;
    }
}
