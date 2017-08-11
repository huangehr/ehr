package com.yihu.ehr.agModel.tj;


/**
 * @author janseny
 * @version 1.0
 * @updated 2017年6月8日
 */

public class TjQuotaChartModel {


    private Long id;
    private String quotaCode;       //关联 tj_quota code
    private String chart_id;        //关联

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuotaCode() {
        return quotaCode;
    }

    public void setQuotaCode(String quotaCode) {
        this.quotaCode = quotaCode;
    }

    public String getChart_id() {
        return chart_id;
    }

    public void setChart_id(String chart_id) {
        this.chart_id = chart_id;
    }
}