package com.yihu.ehr.yihu;


/**
 * Created by hzp on 2016/7/28.
 * 返回对象
 */
public class YihuResponse {

    private int Code;
    private String Message;
    private Object Result;

    /**
     * 10000成功
     */
    public int getCode() {
        return Code;
    }

    public void setCode(int Code) {
        this.Code = Code;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }

    public Object getResult() {
        return Result;
    }

    public void setResult(Object Result) {
        this.Result = Result;
    }
}
