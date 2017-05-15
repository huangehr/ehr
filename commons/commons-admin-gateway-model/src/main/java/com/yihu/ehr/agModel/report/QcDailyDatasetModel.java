package com.yihu.ehr.agModel.report;


/**
 * Created by janseny on 2017/5/8.
 */
public class QcDailyDatasetModel {

    private String id;
    private String reportId;//关联ID
    private String dataset;//数据集code
    private Integer acqFlag;//采集状态  0 - 未采集  1 - 已采集（实收） 验证统计用


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getDataset() {
        return dataset;
    }

    public void setDataset(String dataset) {
        this.dataset = dataset;
    }

    public Integer getAcqFlag() {
        return acqFlag;
    }

    public void setAcqFlag(Integer acqFlag) {
        this.acqFlag = acqFlag;
    }
}
