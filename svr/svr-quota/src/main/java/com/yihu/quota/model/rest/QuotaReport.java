package com.yihu.quota.model.rest;

import com.yihu.quota.model.jpa.TjQuota;

import java.util.List;

/**
 * Created by janseny on 2017/6/30.
 */
public class QuotaReport {

    private TjQuota tjQuota;
    private List<ResultModel> reultModelList ;

    public TjQuota getTjQuota() {
        return tjQuota;
    }

    public void setTjQuota(TjQuota tjQuota) {
        this.tjQuota = tjQuota;
    }

    public List<ResultModel> getReultModelList() {
        return reultModelList;
    }

    public void setReultModelList(List<ResultModel> reultModelList) {
        this.reultModelList = reultModelList;
    }
}
