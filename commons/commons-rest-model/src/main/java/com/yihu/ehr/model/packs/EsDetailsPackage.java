package com.yihu.ehr.model.packs;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * ElasticSearch:json_archives
 * Created by progr1mmer on 2018/4/11.
 */
public class EsDetailsPackage implements Serializable {
    private String _id;
    private String pwd; //密码
    private String remote_path; //fastDfs文件地址
    private Date receive_date; //接收时间
    private Date parse_date; //转化时间
    private Date finish_date; //结束时间
    private Integer archive_status; //解析状态 0 已缓存 1 正在入库 2 入库失败 3 已入库
    private String message; //解析信息
    private String org_code; //机构编码
    private String client_id; //应用ID
    private Integer resourced; //是否已资源化处理 0 未处理 1 已处理
    private String md5_value; //md5值
    private Integer event_type; //事件类型 0 门诊 1 住院 2 体检
    private String event_no; //事件号
    private Date event_date; //事件时间
    private String patient_id; //病人id
    private Integer fail_count; //解析失败次数
    private Integer analyze_status; //质控分析状态
    private Integer analyze_fail_count; //质控分析失败次数
    private Date analyze_date; //质控分析时间

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getPwd() {
        return pwd;
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

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getReceive_date() {
        return receive_date;
    }

    public void setReceive_date(Date receive_date) {
        this.receive_date = receive_date;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getParse_date() {
        return parse_date;
    }

    public void setParse_date(Date parse_date) {
        this.parse_date = parse_date;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getFinish_date() {
        return finish_date;
    }

    public void setFinish_date(Date finish_date) {
        this.finish_date = finish_date;
    }

    public Integer getArchive_status() {
        return archive_status;
    }

    public void setArchive_status(Integer archive_status) {
        this.archive_status = archive_status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOrg_code() {
        return org_code;
    }

    public void setOrg_code(String org_code) {
        this.org_code = org_code;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public Integer getResourced() {
        return resourced;
    }

    public void setResourced(Integer resourced) {
        this.resourced = resourced;
    }

    public String getMd5_value() {
        return md5_value;
    }

    public void setMd5_value(String md5_value) {
        this.md5_value = md5_value;
    }

    public Integer getEvent_type() {
        return event_type;
    }

    public void setEvent_type(Integer event_type) {
        this.event_type = event_type;
    }

    public String getEvent_no() {
        return event_no;
    }

    public void setEvent_no(String event_no) {
        this.event_no = event_no;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getEvent_date() {
        return event_date;
    }

    public void setEvent_date(Date event_date) {
        this.event_date = event_date;
    }

    public String getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(String patient_id) {
        this.patient_id = patient_id;
    }

    public Integer getFail_count() {
        return fail_count;
    }

    public void setFail_count(Integer fail_count) {
        this.fail_count = fail_count;
    }

    public Integer getAnalyze_status() {
        return analyze_status;
    }

    public void setAnalyze_status(Integer analyze_status) {
        this.analyze_status = analyze_status;
    }

    public Integer getAnalyze_fail_count() {
        return analyze_fail_count;
    }

    public void setAnalyze_fail_count(Integer analyze_fail_count) {
        this.analyze_fail_count = analyze_fail_count;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getAnalyze_date() {
        return analyze_date;
    }

    public void setAnalyze_date(Date analyze_date) {
        this.analyze_date = analyze_date;
    }
}
