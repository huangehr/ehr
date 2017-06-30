package com.yihu.quota.model.rest;

import com.yihu.quota.model.jpa.TjQuota;

import java.util.Map;

/**
 * Created by janseny on 2017/6/30.
 */
public class QuotaReport {

    private TjQuota tjQuota;
    private Map<String,Integer> map;

    public TjQuota getTjQuota() {
        return tjQuota;
    }

    public void setTjQuota(TjQuota tjQuota) {
        this.tjQuota = tjQuota;
    }

    public Map<String, Integer> getMap() {
        return map;
    }

    public void setMap(Map<String, Integer> map) {
        this.map = map;
    }
}
