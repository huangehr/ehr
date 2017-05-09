package com.yihu.ehr.agModel.report;

import java.util.List;

/**
 * Created by janseny on 2017/5/9.
 */
public class MetadataModel {

    private  String org_code ;      //机构编码
    private  String  event_time;    //事件时间
    private  String create_date ;   //采集时间
    private  String inner_version ; //适配版本
    private  String  dataset;   //数据集编码
    private  List data;


    public String getOrg_code() {
        return org_code;
    }

    public void setOrg_code(String org_code) {
        this.org_code = org_code;
    }

    public String getEvent_time() {
        return event_time;
    }

    public void setEvent_time(String event_time) {
        this.event_time = event_time;
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

    public String getDataset() {
        return dataset;
    }

    public void setDataset(String dataset) {
        this.dataset = dataset;
    }

    public List getData() {
        return data;
    }

    public void setData(List data) {
        this.data = data;
    }
}
