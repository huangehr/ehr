package com.yihu.ehr.agModel.tj;


import java.util.List;
import java.util.Map;

/**
 * Created by janseny on 2017/6/30.
 */
public class QuotaReportModel {

    private TjQuotaModel tjQuotaModel;
    private List<ReultModel> reultModelList;

    public TjQuotaModel getTjQuotaModel() {
        return tjQuotaModel;
    }

    public void setTjQuotaModel(TjQuotaModel tjQuotaModel) {
        this.tjQuotaModel = tjQuotaModel;
    }

    public List<ReultModel> getReultModelList() {
        return reultModelList;
    }

    public void setReultModelList(List<ReultModel> reultModelList) {
        this.reultModelList = reultModelList;
    }
}
