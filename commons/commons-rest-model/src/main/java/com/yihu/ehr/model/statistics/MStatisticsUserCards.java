package com.yihu.ehr.model.statistics;


/**
 * 数据中心首页-全员人口个案库-健康卡绑定量
 *
 * @author zdm
 * @version 1.0
 * @created 2017.9.12
 */
public class MStatisticsUserCards {

    private Integer totalDemographicsNum;    //总居民数 100
    private String userCardsNum;    //绑卡量 （80(80%)）
    private String nonBindingCardNum;    //未绑卡量（20（20%）=总居民数-绑卡量）

    public Integer getTotalDemographicsNum() {
        return totalDemographicsNum;
    }

    public void setTotalDemographicsNum(Integer totalDemographicsNum) {
        this.totalDemographicsNum = totalDemographicsNum;
    }

    public String getUserCardsNum() {
        return userCardsNum;
    }

    public void setUserCardsNum(String userCardsNum) {
        this.userCardsNum = userCardsNum;
    }

    public String getNonBindingCardNum() {
        return nonBindingCardNum;
    }

    public void setNonBindingCardNum(String nonBindingCardNum) {
        this.nonBindingCardNum = nonBindingCardNum;
    }
}