package com.yihu.ehr.constants;

/**
 * 全局微服务名称枚举。用于Feign及Thrift客户端。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.01.11 8:54
 */
public class MicroServiceName {

    public static final String Adaption = "svr-adaption";
    public static final String App = "svr-app";
    public static final String Dict = "svr-dict";
    public static final String Geography = "svr-geography";
    public static final String Organization = "svr-org";
    public static final String Package = "svr-pack-mgr";
    public static final String Patient = "svr-patient";
    public static final String Security = "svr-security";
    public static final String Standard = "svr-standard";
    public static final String User = "svr-user";
    public static final String Esb = "svr-esb";
}
