package com.yihu.ehr.model.tj;

import java.util.Map;

/**
 * Created by janseny
 */
public class EchartReportModel {
    private String name;//名称
    private int[] data;//柱状和线性图数据集
    private Map<String,String> mapData;//饼状图数据集

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int[] getData() {
        return data;
    }

    public void setData(int[] data) {
        this.data = data;
    }

    public Map<String, String> getMapData() {
        return mapData;
    }

    public void setMapData(Map<String, String> mapData) {
        this.mapData = mapData;
    }
}
