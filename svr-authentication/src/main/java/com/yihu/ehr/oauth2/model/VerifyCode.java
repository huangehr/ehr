package com.yihu.ehr.oauth2.model;

import java.io.Serializable;

/**
 * Created by progr1mmer on 2018/4/18.
 */
public class VerifyCode implements Serializable {

    private String code;
    private int expiresIn;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }
}
