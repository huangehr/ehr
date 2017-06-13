package com.yihu.ehr.model.report.json;

import java.util.List;

/**
 * Created by janseny on 2017/5/9.
 */
public class QcDailyEventsModel {

    private  String org_code ;      //机构编码
    private  String create_date ;   //采集时间
    private  String inner_version ; //适配版本
    private  int  total_outpatient_num;//门诊应收数
    private  int  real_outpatient_num ;//门诊实收数
    private  int  total_hospital_num;//住院应收数
    private  int  real_hospital_num ;//门诊实收数

    private  List<QcDailyEventDetailModel> total_outpatient ;     //门诊应收列表
    private  List<QcDailyEventDetailModel> real_outpatient  ;     //门诊实收列表
    private  List<QcDailyEventDetailModel> total_hospital ;       //住院应收列表
    private  List<QcDailyEventDetailModel> real_hospital ;        //住院实收列表

    public String getOrg_code() {
        return org_code;
    }

    public void setOrg_code(String org_code) {
        this.org_code = org_code;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public String getInner_version() {
        return inner_version;
    }

    public void setInner_version(String inner_version) {
        this.inner_version = inner_version;
    }

    public int getTotal_outpatient_num() {
        return total_outpatient_num;
    }

    public void setTotal_outpatient_num(int total_outpatient_num) {
        this.total_outpatient_num = total_outpatient_num;
    }

    public int getReal_outpatient_num() {
        return real_outpatient_num;
    }

    public void setReal_outpatient_num(int real_outpatient_num) {
        this.real_outpatient_num = real_outpatient_num;
    }

    public int getTotal_hospital_num() {
        return total_hospital_num;
    }

    public void setTotal_hospital_num(int total_hospital_num) {
        this.total_hospital_num = total_hospital_num;
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

    public int getReal_hospital_num() {
        return real_hospital_num;
    }

    public void setReal_hospital_num(int real_hospital_num) {
        this.real_hospital_num = real_hospital_num;
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
