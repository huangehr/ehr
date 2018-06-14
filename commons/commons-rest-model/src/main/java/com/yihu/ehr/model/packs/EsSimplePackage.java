package com.yihu.ehr.model.packs;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by progr1mmer on 2018/4/11.
 */
public class EsSimplePackage implements Serializable {

    private String _id;
    private String pwd; //密码
    private Date receive_date; //接收时间
    private String remote_path; //fastDfs文件地址
    private String client_id; //应用ID
    private String rowkey; //rowkey

    private String patient_id;
    private String event_no;
    private String org_code;
    private String event_date;
    private Integer event_type;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getPwd() {
        return pwd;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getReceive_date() {
        return receive_date;
    }

    public void setReceive_date(java.util.Date receive_date) {
        this.receive_date = receive_date;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getRemote_path() {
        return remote_path;
    }

    public void setRemote_path(String remote_path) {
        this.remote_path = remote_path;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getRowkey() {
        return rowkey;
    }

    public void setRowkey(String rowkey) {
        this.rowkey = rowkey;
    }

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

    public String getOrg_code() {
        return org_code;
    }

    public void setOrg_code(String org_code) {
        this.org_code = org_code;
    }

    public String getEvent_date() {
        return event_date;
    }

    public void setEvent_date(String event_date) {
        this.event_date = event_date;
    }

    public Integer getEvent_type() {
        return event_type;
    }

    public void setEvent_type(Integer event_type) {
        this.event_type = event_type;
    }
}
