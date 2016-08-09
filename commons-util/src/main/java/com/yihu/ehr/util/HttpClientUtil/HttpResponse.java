package com.yihu.ehr.util.HttpClientUtil;

/**
 * add by hzp at 2016-8-9
 */
public class HttpResponse {
    public HttpResponse()
    {

    }

    public HttpResponse(int statusCode, String body) {
        this.statusCode = statusCode;
        this.body = body;
    }

    private int statusCode;
    private String body;


    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
