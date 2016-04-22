package com.yihu.ehr.constants;

import org.springframework.beans.factory.annotation.Value;

/**
 * 全局微服务名称枚举。用于Feign及Thrift客户端。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.01.11 8:54
 */
public class MicroServicePort {
    @Value("${micro.service.adaption.port}")
    public static final String Adaption = "";

    @Value("${micro.service.app.port}")
    public static final String App = "";

    @Value("${micro.service.dict.port}")
    public static final String Dict = "";

    @Value("${micro.service.esb.port}")
    public static final String Esb = "";

    @Value("${micro.service.geography.port}")
    public static final String Geography = "";

    @Value("${micro.service.organization.port}")
    public static final String Organization = "";

    @Value("${micro.service.package.port}")
    public static final String Package = "";

    @Value("${micro.service.patient.port}")
    public static final String Patient = "";

    @Value("${micro.service.security.port}")
    public static final String Security = "";

    @Value("${micro.service.standard.port}")
    public static final String Standard = "";

    @Value("${micro.service.user.port}")
    public static final String User = "";
}
