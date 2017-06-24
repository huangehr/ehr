package com.yihu.ehr.constants;

/**
 * 全局微服务名称枚举。用于Feign及Thrift客户端。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.01.11 8:54
 */
public class MicroServices {
    public static final String Discovery = "svr-discovery";
    public static final String Configuration = "svr-configuration";

    //以下几个微服务聚合svr-ehr-basic
    public static final String Application = "svr-ehr-basic";
    public static final String Geography = "svr-ehr-basic";
    public static final String Patient = "svr-ehr-basic";
    public static final String Portal = "svr-ehr-basic";
    public static final String Security = "svr-ehr-basic";
    public static final String User = "svr-ehr-basic";
    public static final String Family = "svr-ehr-basic";
    public static final String FileResource = "svr-ehr-basic";
    public static final String Dictionary = "svr-ehr-basic";
    public static final String Organization = "svr-ehr-basic";

    //以下几个微服务聚合svr-standard
    public static final String Adaption = "svr-standard";
    public static final String Standard = "svr-standard";
    public static final String SpecialDict = "svr-standard";

    public static final String Resource = "svr-resource";
    public static final String ESB = "svr-esb";

    public static final String HealthProfile = "svr-health-profile";
    public static final String PackageResolve = "svr-pack-resolve";
    public static final String Package = "svr-pack-mgr";
    public static final String Cipher = "svr-cipher";
    public static final String ArchiveSecurity = "svr-archive-security";

    public static final String Authentication = "svr-authentication";
    public static final String Redis = "svr-redis";
    public static final String Statistics = "svr-protal-statistics";
    public static final String Quota = "svr-quota";

    public static final String LogCollection = "svr-logCollection";
}
