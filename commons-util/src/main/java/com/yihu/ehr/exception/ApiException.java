package com.yihu.ehr.exception;

import com.yihu.ehr.constrant.ErrorCode;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.20 16:05
 */
public class ApiException extends RuntimeException {
    private static final long serialVersionUID = 1464313464798414L;

    private ErrorCode errCode;
    private String errMsg;

    public ErrorCode getErrorCode() {
        return errCode;
    }

    public void setErrCode(ErrorCode errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public ApiException(ErrorCode errCode) {
        this.errCode = errCode;
    }

    public ApiException(ErrorCode errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }
}
