package com.yihu.ehr.oauth2.model;

import java.io.Serializable;

/**
 * Created by progr1mmer on 2018/4/18.
 */
public class VerifyCode implements Serializable {
    /**
     * 验证码
     */
    private String code;
    /**
     * 验证码有限期
     */
    private int expiresIn;
    /**
     * 验证码下一次请求时间
     */
    private int nextRequestTime;

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

    public int getNextRequestTime() {
        return nextRequestTime;
    }

    public void setNextRequestTime(int nextRequestTime) {
        this.nextRequestTime = nextRequestTime;
    }
}
