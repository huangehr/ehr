package com.yihu.ehr.model.tj;

import java.util.Map;

/**
 * Created by janseny
 */
public class EchartReportModel {
    private String name;//名称
    private String[] xData;//柱状和线性图数据集
    private int[] yData;//柱状和线性图数据集
    private Map<String,String> mapData;//饼状图数据集

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

    public int[] getyData() {
        return yData;
    }

    public void setyData(int[] yData) {
        this.yData = yData;
    }

    public Map<String, String> getMapData() {
        return mapData;
    }

    public void setMapData(Map<String, String> mapData) {
        this.mapData = mapData;
    }
}
