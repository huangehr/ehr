package com.yihu.ehr.profile.core;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * 轻量级健康档案。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.04.13 14:57
 */
public class LightWeightProfile extends Profile {
    protected Map<String, String> urls = new TreeMap<>();               // 数据集URL

    protected Map<String, ProfileDataSet> dataSets = new TreeMap<>();   // 数据集摘要

    protected Date expiredDate;

    public ProfileType getType(){
        return ProfileType.LightWeight;
    }

    public Map<String, String> getUrls() {
        return urls;
    }

    public void setUrls(Map<String, String> urls) {
        this.urls = urls;
    }

    public Map<String, ProfileDataSet> getDataSets() {
        return dataSets;
    }

    public void setDataSets(Map<String, ProfileDataSet> dataSets) {
        this.dataSets = dataSets;
    }

    public Date getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(Date expiredDate) {
        this.expiredDate = expiredDate;
    }
}
