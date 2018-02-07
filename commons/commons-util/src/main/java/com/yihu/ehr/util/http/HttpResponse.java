package com.yihu.ehr.util.http;

/**
 * Utils - Http请求辅助类，简化页面页面判断逻辑
 * Created by progr1mmer on 2018/1/16.
 */
public class HttpResponse {

    private int status;
    private String content;

    public HttpResponse(int status, String content) {
        this.status = status;
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isSuccessFlg() {
        return status == 200;
    }

    public String getErrorMsg() {
        return content;
    }

}
