package com.yihu.ehr.medicalRecords.comom;


/**
 * Created by hzp on 2016/8/4.
 * 返回对象
 */
public class WlyyResponse {

    private int status;
    private String msg;
    private Object data;

    /**
     * 200成功
     */
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
