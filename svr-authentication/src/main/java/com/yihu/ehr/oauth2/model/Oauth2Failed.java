package com.yihu.ehr.oauth2.model;

import java.io.Serializable;

/**
 * Created by progr1mmer on 2018/4/21.
 */
public class Oauth2Failed implements Serializable {

    private int errorCode;

    private String errorMsg;

    public Oauth2Failed(){

    }

    public Oauth2Failed(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
