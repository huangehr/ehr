package com.yihu.quota.model.rest;

import com.yihu.quota.model.jpa.TjQuota;

import java.util.List;
import java.util.Map;

/**
 * Created by janseny on 2017/6/30.
 */
public class QuotaReport {

    private TjQuota tjQuota;
    private List<ReultModel> reultModelList ;

    public TjQuota getTjQuota() {
        return tjQuota;
    }

    public void setTjQuota(TjQuota tjQuota) {
        this.tjQuota = tjQuota;
    }

    public List<ReultModel> getReultModelList() {
        return reultModelList;
    }

    public void setReultModelList(List<ReultModel> reultModelList) {
        this.reultModelList = reultModelList;
    }
}
