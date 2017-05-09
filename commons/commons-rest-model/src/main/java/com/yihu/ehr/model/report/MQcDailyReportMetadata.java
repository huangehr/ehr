package com.yihu.ehr.model.report;

import java.util.Date;

/**
 * Created by janseny on 2017/5/8.
 */
public class MQcDailyReportMetadata {

    private String id;
    private String datasetId;//关联ID
    private String metadate;//数据元inner_code
    private String orgCode;//机构编码
    private Date eventTime;//事件时间
    private String innerVersion;//适配标准版本
    private String dataset;//数据集code
    private Date createDate;//采集日期
    private String totalQty;//应采数据元数量
    private Integer errorQty;//错误数据元数量
    private String errCode;//错误代码
    private Integer acqFlag;//采集状态  0 - 未采集  1 - 已采集（实收） 验证统计用


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(String datasetId) {
        this.datasetId = datasetId;
    }

    public String getMetadate() {
        return metadate;
    }

    public void setMetadate(String metadate) {
        this.metadate = metadate;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public Date getEventTime() {
        return eventTime;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    public String getInnerVersion() {
        return innerVersion;
    }

    public void setInnerVersion(String innerVersion) {
        this.innerVersion = innerVersion;
    }

    public String getDataset() {
        return dataset;
    }

    public void setDataset(String dataset) {
        this.dataset = dataset;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(String totalQty) {
        this.totalQty = totalQty;
    }

    public Integer getErrorQty() {
        return errorQty;
    }

    public void setErrorQty(Integer errorQty) {
        this.errorQty = errorQty;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public Integer getAcqFlag() {
        return acqFlag;
    }

    public void setAcqFlag(Integer acqFlag) {
        this.acqFlag = acqFlag;
    }
}
