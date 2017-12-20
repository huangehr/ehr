package com.yihu.quota.model.rest;

import com.yihu.quota.model.jpa.TjQuota;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/12/19.
 */
public class QuotaTreeModel {
    private TjQuota tjQuota;
    private List<Map<String, Object>> mapList;
    private List<TjQuota> childern;

    public TjQuota getTjQuota() {
        return tjQuota;
    }

    public void setTjQuota(TjQuota tjQuota) {
        this.tjQuota = tjQuota;
    }

    public List<Map<String, Object>> getMapList() {
        return mapList;
    }

    public void setMapList(List<Map<String, Object>> mapList) {
        this.mapList = mapList;
    }

    public List<TjQuota> getChildern() {
        return childern;
    }

    public void setChildern(List<TjQuota> childern) {
        this.childern = childern;
    }
}
