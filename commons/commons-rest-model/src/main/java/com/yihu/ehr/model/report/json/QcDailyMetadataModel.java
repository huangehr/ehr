package com.yihu.ehr.model.report.json;

import java.util.Date;

/**
 * Created by janseny on 2017/5/9.
 */
public class QcDailyMetadataModel {

    private  String orgCode ;      //机构编码
    private Date eventTime;    //事件时间
    private  Date createDate ;   //采集时间
    private  String innerVersion ; //适配版本
    private  String  dataset;   //数据集编码
    private Integer totalQty;//应采数据元数量
    private Integer errorQty;//错误数据元数量
    private String errCode;//错误代码
    private String metadate;//数据元inner_code
    private Integer acqFlag;
    private String datasetId;


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

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
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

    public Integer getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(Integer totalQty) {
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

    public String getMetadate() {
        return metadate;
    }

    public void setMetadate(String metadate) {
        this.metadate = metadate;
    }

    public String getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(String datasetId) {
        this.datasetId = datasetId;
    }

    public Integer getAcqFlag() {
        return acqFlag;
    }

    public void setAcqFlag(Integer acqFlag) {
        this.acqFlag = acqFlag;
    }
}
