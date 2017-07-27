package com.yihu.ehr.entity.report;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by janseny on 2017/5/8.
 */
@Entity
@Table(name = "qc_daily_report_metadata", schema = "", catalog = "healtharchive")
public class QcDailyReportMetadata {

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
    private Date addDate;   //添加时间


    @Id
    @GenericGenerator(name="systemUUID",strategy="uuid")
    @GeneratedValue(generator="systemUUID")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Basic
    @Column(name = "org_code")
    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    @Basic
    @Column(name = "create_date")
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Basic
    @Column(name = "inner_version")
    public String getInnerVersion() {
        return innerVersion;
    }

    public void setInnerVersion(String innerVersion) {
        this.innerVersion = innerVersion;
    }

    @Basic
    @Column(name = "dataset_id")
    public String getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(String datasetId) {
        this.datasetId = datasetId;
    }

    @Basic
    @Column(name = "metadate")
    public String getMetadate() {
        return metadate;
    }

    public void setMetadate(String metadate) {
        this.metadate = metadate;
    }

    @Column(name = "event_time")
    public Date getEventTime() {
        return eventTime;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    @Basic
    @Column(name = "dataset")
    public String getDataset() {
        return dataset;
    }

    public void setDataset(String dataset) {
        this.dataset = dataset;
    }

    @Basic
    @Column(name = "total_qty")
    public String getTotalQty() {
        return totalQty;
    }


    public void setTotalQty(String totalQty) {
        this.totalQty = totalQty;
    }

    @Basic
    @Column(name = "error_qty")
    public Integer getErrorQty() {
        return errorQty;
    }

    public void setErrorQty(Integer errorQty) {
        this.errorQty = errorQty;
    }

    @Basic
    @Column(name = "err_code")
    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    @Basic
    @Column(name = "acq_flag")
    public Integer getAcqFlag() {
        return acqFlag;
    }

    public void setAcqFlag(Integer acqFlag) {
        this.acqFlag = acqFlag;
    }

    @Basic
    @Column(name = "add_date")
    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }
}
