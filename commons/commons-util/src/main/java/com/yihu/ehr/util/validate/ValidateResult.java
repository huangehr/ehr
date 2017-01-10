package com.yihu.ehr.util.validate;

/**
 * @author lincl
 * @version 1.0
 * @created 2016/3/2
 */
public class ValidateResult {
    public static int ERR_NO_NULL = 1;
    public static int ERR_OUT_OF_LENGTH = 2;

    private int errCode = 0;
    private String msg = "";
    private String filed = "";
    private boolean rs = true;

    public ValidateResult() {

    }

    public static ValidateResult success(){

        return new ValidateResult();
    }

    public static ValidateResult failedOfNull(String msg, String filed){

        return failed(ERR_NO_NULL, msg, filed);
    }

    public static ValidateResult failedOfOutLen(String msg, String filed){

        return failed(ERR_OUT_OF_LENGTH, msg, filed);
    }

    public static ValidateResult failed(int errCode, String msg, String filed){

        return new ValidateResult(errCode, msg, filed, false);
    }

    public ValidateResult(int errCode, String msg, String filed, boolean rs) {
        this.errCode = errCode;
        this.msg = msg;
        this.filed = filed;
        this.rs = rs;
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getFiled() {
        return filed;
    }

    public void setFiled(String filed) {
        this.filed = filed;
    }

    public boolean isRs() {
        return rs;
    }

    public void setRs(boolean rs) {
        this.rs = rs;
    }
}
