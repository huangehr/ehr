package com.yihu.ehr.entity.quota;

import javax.persistence.*;

/**
 * Created by janseny on 2017/8/3.
 */
@Entity
@Table(name = "tj_quota_chart")
public class TjQuotaChart {

    private Integer id;
    private String quotaCode;
    private Integer chartId;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "quota_code", nullable = false)
    public String getQuotaCode() {
        return quotaCode;
    }

    public void setQuotaCode(String quotaCode) {
        this.quotaCode = quotaCode;
    }

    @Column(name = "chart_id", nullable = false)
    public Integer getChartId() {
        return chartId;
    }

    public void setChartId(Integer chartId) {
        this.chartId = chartId;
    }
}
