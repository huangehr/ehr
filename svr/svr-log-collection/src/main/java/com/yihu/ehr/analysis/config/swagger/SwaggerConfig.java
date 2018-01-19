package com.yihu.ehr.analysis.config.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    public static final String Iot_API = "quota";

    @Bean
    public Docket iotAPI() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName(Iot_API)
                .useDefaultResponseMessages(false)
                .forCodeGeneration(false)
                .pathMapping("/")
                .select()
                .paths(or(
                        regex("/quota/.*")
                ))
                .build()
                .apiInfo(iotApiInfo());
    }

    private ApiInfo iotApiInfo() {
        ApiInfo iotInfo = new ApiInfo("基卫2.0API",
                "EHR API，提供日志相关服务。",
                "1.0",
                "No terms of service",
                "wenfujian@jkzl.com",
                "The Apache License, Version 2.0",
                "http://www.apache.org/licenses/LICENSE-2.0.html"
        );

        return iotInfo;
    }


}