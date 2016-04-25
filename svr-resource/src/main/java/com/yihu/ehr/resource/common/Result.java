package com.yihu.ehr.resource.common;

/**
 * Created by hzp on 20160401
 * 基础对象
 */
public class Result {
    //是否成功
    private boolean successFlg = true;
    //返回消息
    private String message;
    //错误代码
    private int errorCode;

    public boolean isSuccessFlg() {
        return successFlg;
    }

    public void setSuccessFlg(boolean successFlg) {
        this.successFlg = successFlg;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * 返回错误消息
     * @return
     */
    public static Result error(String message) {
        Result re= new Result();
        re.successFlg = false;
        re.message = message;
        return re;
    }

    /**
     * 返回成功消息
     * @return
     */
    public static Result success(String message) {
        Result re= new Result();
        re.successFlg = true;
        re.message = message;
        return re;
    }
}
