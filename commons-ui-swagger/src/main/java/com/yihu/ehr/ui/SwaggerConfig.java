package com.yihu.ehr.ui;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
@ComponentScan("com.yihu.ehr.*.controller")
public class SwaggerConfig {
    public static final String PUBLIC_API = "Public";
    public static final String ADMIN_API = "Private";

    @Bean
    public Docket publicAPI() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName(PUBLIC_API)
                .genericModelSubstitutes(DeferredResult.class)
                .useDefaultResponseMessages(false)
                .forCodeGeneration(true)
                .pathMapping("/")
                .select()
                .paths(or(regex("/api.*")))
                .build()
                .apiInfo(publicApiInfo());
    }

    @Bean
    public Docket privateAPI() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName(ADMIN_API)
                .genericModelSubstitutes(DeferredResult.class)
                .useDefaultResponseMessages(false)
                .forCodeGeneration(false)
                .pathMapping("/")
                .select()
                .paths(or(regex("/admin.*")))
                .build()
                .apiInfo(privateAPIInfo());
    }

    @Bean
    SecurityConfiguration security() {
        return new SecurityConfiguration(
                "test-app-client-id",
                "test-app-realm",
                "test-app",
                "ac04-47ec-9a9a-7c47bbcbbbd1");
    }

    private ApiInfo publicApiInfo() {
        ApiInfo apiInfo = new ApiInfo("健康档案平台开放API",
                "健康档案平台开放API，提供健康档案服务。",
                "1.0",
                "NO terms of service",
                "wenfujian@jkzl.com",
                "The Apache License, Version 2.0",
                "http://www.apache.org/licenses/LICENSE-2.0.html"
        );

        return apiInfo;
    }

    private ApiInfo privateAPIInfo() {
        ApiInfo apiInfo = new ApiInfo("健康档案平台私有API",
                "健康档案平台私有API。",
                "1.0",
                "NO terms of service",
                "wenfujian@jkzl.com",
                "The Apache License, Version 2.0",
                "http://www.apache.org/licenses/LICENSE-2.0.html"
        );

        return apiInfo;
    }
}