package com.yihu.ehr.agModel.report;

import java.util.Date;
import java.util.List;

/**
 * Created by janseny on 2017/5/8.
 */
public class QcDailyDatasetsModel {

    private  String org_code ;      //机构编码
    private  String create_date ;   //采集时间
    private  String inner_version ; //适配版本
    private  String  event_time;    //事件时间
    private  int  total_hospital_num;   //应采数据集数
    private  int  real_hospital_num ;   //实采数据集数
    private List<QcDailyDatasetModel> qcDailyDatasetModels;

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

    public String getEvent_time() {
        return event_time;
    }

    public void setEvent_time(String event_time) {
        this.event_time = event_time;
    }

    public int getTotal_hospital_num() {
        return total_hospital_num;
    }

    public void setTotal_hospital_num(int total_hospital_num) {
        this.total_hospital_num = total_hospital_num;
    }

    public int getReal_hospital_num() {
        return real_hospital_num;
    }

    public void setReal_hospital_num(int real_hospital_num) {
        this.real_hospital_num = real_hospital_num;
    }

    public List<QcDailyDatasetModel> getQcDailyDatasetModels() {
        return qcDailyDatasetModels;
    }

    public void setQcDailyDatasetModels(List<QcDailyDatasetModel> qcDailyDatasetModels) {
        this.qcDailyDatasetModels = qcDailyDatasetModels;
    }
}
