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
}
