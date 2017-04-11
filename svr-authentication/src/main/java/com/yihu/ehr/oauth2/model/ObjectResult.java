package com.yihu.ehr.oauth2.model;


/**
 * Created by hzp on 20170315.
 */
public class ObjectResult extends Result {
    private Object data;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public ObjectResult()
    {}

    public ObjectResult(boolean successFlg,String message){
        super.setSuccessFlg(successFlg);
        super.setMessage(message);
    }

}
