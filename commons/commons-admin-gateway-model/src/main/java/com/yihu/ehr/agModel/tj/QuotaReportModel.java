package com.yihu.ehr.agModel.tj;


import java.util.Map;

/**
 * Created by janseny on 2017/6/30.
 */
public class QuotaReportModel {

    private TjQuotaModel tjQuotaModel;
    private Map<String,Integer> map;

    public TjQuotaModel getTjQuotaModel() {
        return tjQuotaModel;
    }

    public void setTjQuotaModel(TjQuotaModel tjQuotaModel) {
        this.tjQuotaModel = tjQuotaModel;
    }

    public Map<String, Integer> getMap() {
        return map;
    }

    public void setMap(Map<String, Integer> map) {
        this.map = map;
    }
}
