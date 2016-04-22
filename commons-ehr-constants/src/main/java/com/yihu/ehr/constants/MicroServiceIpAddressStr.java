package com.yihu.ehr.constants;

import org.springframework.beans.factory.annotation.Value;

/**
 * 全局微服务名称枚举。用于Feign及Thrift客户端。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.01.11 8:54
 */
public class MicroServiceIpAddressStr {

    @Value("${micro.service.adaption.address-str}")
    public static final String Adaption = "";

    @Value("${micro.service.app.address-str}")
    public static final String App = "";

    @Value("${micro-.service.dict.address-str}")
    public static final String Dict = "";

    @Value("${micro.service.esb.address-str}")
    public static final String Esb = "";

    @Value("${micro.service.geography.address-str}")
    public static final String Geography = "";

    @Value("${micro.service.organization.address-str}")
    public static final String Organization = "";

    @Value("${micro.service.package.address-str}")
    public static final String Package = "";

    @Value("${micro.service.patient.address-str}")
    public static final String Patient = "";

    @Value("${micro.service.security.address-str}")
    public static final String Security = "";

    @Value("${micro.service.standard.address-str}")
    public static final String Standard = "";

    @Value("${micro.service.user.address-str}")
    public static final String User = "";
}
