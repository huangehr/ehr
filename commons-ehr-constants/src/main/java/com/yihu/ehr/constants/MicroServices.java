package com.yihu.ehr.constants;

/**
 * 全局微服务枚举。用于Feign及Thrift客户端。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.01.11 8:54
 */
public enum MicroServices {
    Discovery("svr-discovery"),

    Configuration("svr-configuration"),

    Dict("svr-dict"),

    User("svr-user"),

    Organization("svr-org"),

    SSO("svr-sso"),

    Standard("svr-standard"),

    Archive("svr-archive"),

    PackageManager("PACKAGE-MANAGER");

    private String serviceName;
    private MicroServices(String serviceName){
        this.serviceName = serviceName;
    }
}
