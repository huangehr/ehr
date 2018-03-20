package com.yihu.ehr.ui;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
@ComponentScan("com.yihu.ehr.*.controller")
//@Profile({"dev", "test"})
public class SwaggerConfig {

    public static final String API_VERSION = "v1.0";
    public static final String PUBLIC_API = "Default";
    public static final String PRIVATE_API = "Private";
    public static final String LEGACY_API = "Legacy";

    @Bean
    public Docket publicAPI() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName(PUBLIC_API)
                .genericModelSubstitutes(DeferredResult.class)
                .useDefaultResponseMessages(false)
                .forCodeGeneration(true)
                .pathMapping("/")
                .select()
                .paths(or(regex("/api.*"),
                        regex("/oauth/.*")))
                .build()
                .apiInfo(publicApiInfo());
    }

    @Bean
    public Docket privateAPI() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName(PRIVATE_API)
                .genericModelSubstitutes(DeferredResult.class)
                .useDefaultResponseMessages(false)
                .forCodeGeneration(false)
                .pathMapping("/")
                .select()
                .paths(or(regex("/admin/.*"),
                        regex("/restart/.*"),
                        regex("/env/.*"),
                        regex("/trace/.*"),
                        regex("/health/.*"),
                        regex("/shutdown/.*"),
                        regex("/refresh/.*"),
                        regex("/pause/.*"),
                        regex("/dump/.*"),
                        regex("/resume/.*"),
                        regex("/docs/.*"),
                        regex("/actuator/.*"),
                        regex("/info/.*"),
                        regex("/log/.*"),
                        regex("/autoconfig/.*")))
                .build()
                .apiInfo(privateAPIInfo());
    }

    @Bean
    public Docket legacyAPI(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName(LEGACY_API)
                .genericModelSubstitutes(DeferredResult.class)
                .useDefaultResponseMessages(false)
                .forCodeGeneration(false)
                .pathMapping("/")
                .select()
                .paths(or(regex("/rest/v.*")))
                .build()
                .apiInfo(legacyApiInfo());
    }

    /**
     * 测试client信息。
     *
     * @return
     */
    /*@Bean
    SecurityConfiguration security() {
        return new SecurityConfiguration(
                "ehr-browser",
                "secret123",
                "ROLE_CLIENT",
                "test-app",
                "ac04-47ec-9a9a-7c47bbcbbbd1");
    }*/

    private ApiInfo publicApiInfo() {
        return new ApiInfoBuilder()
                .title("医疗云平台API")
                .description("医疗云平台API，提供后端基础数据接口")
                .version(API_VERSION)
                .termsOfServiceUrl("http://www.jkzl.com")
                .contact(new Contact("Jkzl Xiamen R & D Center Platform Development.", "http://www.jkzl.com", "jzkl@jkzl.com"))
                .license("The Apache License, Version 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
                .build();

        /*ApiInfo apiInfo = new ApiInfo("EHR医疗云平台API",
                "健康档案平台开放API，提供健康档案服务。",
                "1.0",
                "No terms of service",
                "wenfujian@jkzl.com",
                "The Apache License, Version 2.0",
                "http://www.apache.org/licenses/LICENSE-2.0.html"
        );

        return apiInfo;*/
    }

    private ApiInfo privateAPIInfo() {
        return new ApiInfoBuilder()
                .title("医疗云平台API")
                .description("医疗云平台API，提供后端基础数据接口")
                .version(API_VERSION)
                .termsOfServiceUrl("http://www.jkzl.com")
                .contact(new Contact("Jkzl Xiamen R & D Center Platform Development.", "http://www.jkzl.com", "jzkl@jkzl.com"))
                .license("The Apache License, Version 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
                .build();

        /*ApiInfo apiInfo = new ApiInfo("健康档案平台私有API",
                "健康档案平台私有API。",
                "1.0",
                "No terms of service",
                "wenfujian@jkzl.com",
                "The Apache License, Version 2.0",
                "http://www.apache.org/licenses/LICENSE-2.0.html"
        );
        return apiInfo;*/
    }

    private ApiInfo legacyApiInfo() {
        return new ApiInfoBuilder()
                .title("医疗云平台API")
                .description("医疗云平台API，提供后端基础数据接口")
                .version(API_VERSION)
                .termsOfServiceUrl("http://www.jkzl.com")
                .contact(new Contact("Jkzl Xiamen R & D Center Platform Development.", "http://www.jkzl.com", "jzkl@jkzl.com"))
                .license("The Apache License, Version 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
                .build();

        /*ApiInfo apiInfo = new ApiInfo("健康档案平台开放API",
                "健康档案平台开放API(历史兼容接口)，此部分API因为设计不规范，但已经发布，所以继续提供兼容。",
                "1.0",
                "No terms of service",
                "wenfujian@jkzl.com",
                "The Apache License, Version 2.0",
                "http://www.apache.org/licenses/LICENSE-2.0.html"
        );
        return apiInfo;*/
    }

}