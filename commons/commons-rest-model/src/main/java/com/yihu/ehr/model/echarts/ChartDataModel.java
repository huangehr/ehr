package com.yihu.ehr.model.echarts;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/12/12.
 */
public class ChartDataModel implements Serializable {
    private List<Map<String, Object>> list;
    private ChartDataModel children;

    public List<Map<String, Object>> getList() {
        return list;
    }

    public void setList(List<Map<String, Object>> list) {
        this.list = list;
    }

    public ChartDataModel getChildren() {
        return children;
    }

    public void setChildren(ChartDataModel children) {
        this.children = children;
    }
}
