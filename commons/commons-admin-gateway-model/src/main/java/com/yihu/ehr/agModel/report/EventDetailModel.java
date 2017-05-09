package com.yihu.ehr.agModel.report;

/**
 * Created by janseny on 2017/5/9.
 */
public class EventDetailModel {

    private  String  patient_id;   //病人id
    private  String  event_no;      //事件号
    private  String  event_time;    //事件时间

    public String getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(String patient_id) {
        this.patient_id = patient_id;
    }

    public String getEvent_no() {
        return event_no;
    }

    public void setEvent_no(String event_no) {
        this.event_no = event_no;
    }

    public String getEvent_time() {
        return event_time;
    }

    public void setEvent_time(String event_time) {
        this.event_time = event_time;
    }
}
