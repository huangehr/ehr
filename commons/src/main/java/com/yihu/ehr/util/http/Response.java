package com.yihu.ehr.util.http;

/**
 * @author Air
 * @version 1.0
 * @created 2015.07.02 14:43
 */
public class Response {
    public Response(int statusCode, String body) {
        this.statusCode = statusCode;
        this.body = body;
    }

    public final int statusCode;// e.g. 200
    public final String body;
//    public final String status; // e.g. "200 OK"
//    public final String proto; // e.g. "HTTP/1.0"
//    public final int protoMajor;     // e.g. 1
//    public final int protoMinor;    // e.g. 0
}
