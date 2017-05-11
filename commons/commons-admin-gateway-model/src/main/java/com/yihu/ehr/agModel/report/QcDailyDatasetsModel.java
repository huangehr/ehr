package com.yihu.ehr.agModel.report;

import java.util.Date;
import java.util.List;

/**
 * Created by janseny on 2017/5/8.
 */
public class QcDailyDatasetsModel {

    private  String orgCode ;      //机构编码
    private  String createDate ;   //采集时间
    private  String innerVersion ; //适配版本
    private  String  eventTime;    //事件时间
    private  int  totalHospitalNum;   //应采数据集数
    private  int  realHospitalNum ;   //实采数据集数
    private List<QcDailyDatasetModel> qcDailyDatasetModels;

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getInnerVersion() {
        return innerVersion;
    }

    public void setInnerVersion(String innerVersion) {
        this.innerVersion = innerVersion;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public int getTotalHospitalNum() {
        return totalHospitalNum;
    }

    public void setTotalHospitalNum(int totalHospitalNum) {
        this.totalHospitalNum = totalHospitalNum;
    }

    public int getRealHospitalNum() {
        return realHospitalNum;
    }

    public void setRealHospitalNum(int realHospitalNum) {
        this.realHospitalNum = realHospitalNum;
    }

    public List<QcDailyDatasetModel> getQcDailyDatasetModels() {
        return qcDailyDatasetModels;
    }

    public void setQcDailyDatasetModels(List<QcDailyDatasetModel> qcDailyDatasetModels) {
        this.qcDailyDatasetModels = qcDailyDatasetModels;
    }
}
