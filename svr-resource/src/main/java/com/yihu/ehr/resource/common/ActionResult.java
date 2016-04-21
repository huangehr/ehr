package com.yihu.ehr.resource.common;


/**
 * Created by chenweida on 2015/12/15.
 */
public class ActionResult extends Result {
    private Object data;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public ActionResult()
    {}

    public ActionResult(boolean successFlg,String message){
        super.setSuccessFlg(successFlg);
        super.setMessage(message);
    }

}
