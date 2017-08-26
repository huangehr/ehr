package com.yihu.ehr.model.resource;


import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/12.
 */
public class MChartInfoModel {
    private String title;
    private String option;
    private List<MReportDimension> listMap;

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

    public List<MReportDimension> getListMap() {
        return listMap;
    }

    public void setListMap(List<MReportDimension> listMap) {
        this.listMap = listMap;
    }
}
