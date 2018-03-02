package com.yihu.ehr.pack.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *   ehr使用 feginClient 调用eip(涉及到app应用,标准等) 时,需要此类,增加请求头信息
 */
@Configuration
public class TenantConfiguration {

    @Value("${eip.tenant}")
    private String tenant;

    @Bean
    public RequestInterceptor tenantInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                requestTemplate.header("tenant_name", tenant);
            }
        };
    }
}