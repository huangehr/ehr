package com.yihu.ehr.constants;

/**
 * 全局微服务名称枚举。用于Feign及Thrift客户端。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.01.11 8:54
 */
public class MicroServices {

    public static final String Configuration = "svr-configuration";

    //以下几个微服务聚合svr-basic
    public static final String Basic = "svr-basic";
    public static final String Application = "svr-basic";
    public static final String Geography = "svr-basic";
    public static final String Patient = "svr-basic";
    public static final String Portal = "svr-basic";
    public static final String Security = "svr-basic";
    public static final String User = "svr-basic";
    public static final String Family = "svr-basic";
    public static final String FileResource = "svr-basic";
    public static final String Dictionary = "svr-basic";
    public static final String EsbDictionary = "hos-admin";
    public static final String Organization = "svr-basic";

    //以下几个微服务聚合svr-standard
    public static final String Adaption = "svr-standard";
    public static final String Standard = "hos-admin";
    public static final String StandardSource = "svr-standard";
    public static final String SpecialDict = "hos-admin";

    public static final String Resource = "svr-resource";

    public static final String ESB = "svr-esb";

    public static final String HealthProfile = "svr-health-profile";

    public static final String PackageResolve = "svr-pack-resolve";

    public static final String Package = "svr-pack-mgr";

    public static final String Authentication = "svr-authentication";

    public static final String Redis = "svr-redis";

    public static final String StdRedis = "hos-admin";

    public static final String Quota = "svr-quota";

    public static final String Dfs = "svr-dfs";

    public static final String Cipher = "svr-cipher";
    public static final String ArchiveSecurity = "svr-archive-security";
    public static final String Statistics = "svr-protal-statistics";
    public static final String Discovery = "svr-discovery";
    public static final String LogCollection = "svr-logCollection";

    public static final String Analyzer = "svr-pack-analyzer";

    public static final String FzGateway = "ag-admin";
    public static final String AgZuul = "ag-zuul";

}
