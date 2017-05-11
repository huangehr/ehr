package com.yihu.ehr.agModel.report;

import java.util.List;

/**
 * Created by janseny on 2017/5/9.
 */
public class QcDailyEventsModel {

    private  String orgCode ;      //机构编码
    private  String createDate ;   //采集时间
    private  String innerVersion ; //适配版本
    private  int  totalOutpatientNum;//门诊应收数
    private  int  realOutpatientNum ;//门诊实收数
    private  int  totalHospitalNum;   //住院应收数
    private  int  realHospitalNum ;   //住院实收数

    private  List<QcDailyEventDetailModel> total_outpatient ;     //门诊应收列表
    private  List<QcDailyEventDetailModel> real_outpatient  ;     //门诊实收列表
    private  List<QcDailyEventDetailModel> total_hospital ;       //住院应收列表
    private  List<QcDailyEventDetailModel> real_hospital ;        //住院实收列表

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

    public int getTotalOutpatientNum() {
        return totalOutpatientNum;
    }

    public void setTotalOutpatientNum(int totalOutpatientNum) {
        this.totalOutpatientNum = totalOutpatientNum;
    }

    public int getRealOutpatientNum() {
        return realOutpatientNum;
    }

    public void setRealOutpatientNum(int realOutpatientNum) {
        this.realOutpatientNum = realOutpatientNum;
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

    public List<QcDailyEventDetailModel> getTotal_outpatient() {
        return total_outpatient;
    }

    public void setTotal_outpatient(List<QcDailyEventDetailModel> total_outpatient) {
        this.total_outpatient = total_outpatient;
    }

    public List<QcDailyEventDetailModel> getReal_outpatient() {
        return real_outpatient;
    }

    public void setReal_outpatient(List<QcDailyEventDetailModel> real_outpatient) {
        this.real_outpatient = real_outpatient;
    }

    public List<QcDailyEventDetailModel> getTotal_hospital() {
        return total_hospital;
    }

    public void setTotal_hospital(List<QcDailyEventDetailModel> total_hospital) {
        this.total_hospital = total_hospital;
    }

    public List<QcDailyEventDetailModel> getReal_hospital() {
        return real_hospital;
    }

    public void setReal_hospital(List<QcDailyEventDetailModel> real_hospital) {
        this.real_hospital = real_hospital;
    }
}
