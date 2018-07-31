package com.yihu.ehr.ui;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
//@Profile({"dev", "test"})
public class SwaggerConfig {

    public static final String API_VERSION = "v1.0";

    @Bean
    public Docket publicAPI() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("Default")
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
                .groupName("Private")
                .useDefaultResponseMessages(false)
                .forCodeGeneration(false)
                .pathMapping("/")
                .select()
                .paths(or(regex("/archaius"),
                        regex("/loggers*"),
                        regex("/mappings"),
                        regex("/pause"),
                        regex("/beans"),
                        regex("/health"),
                        regex("/resume"),
                        regex("/heapdump"),
                        regex("/env*"),
                        regex("/shutdown"),
                        regex("/configprops"),
                        regex("/auditevents"),
                        regex("/restart"),
                        regex("/trace"),
                        regex("/metrics*"),
                        regex("/refresh"),
                        regex("/dump"),
                        regex("/autoconfig"),
                        regex("/info"),
                        regex("/features")))
                .build()
                .apiInfo(privateAPIInfo());
    }

    @Bean
    public Docket legacyAPI(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("Legacy")
                .useDefaultResponseMessages(false)
                .forCodeGeneration(false)
                .pathMapping("/")
                .select()
                .paths(or(regex("/legacy/rest/v.*")))
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
                .termsOfServiceUrl("https://www.yihu.com")
                .contact(new Contact("Jkzl Xiamen R & D Center Platform Development.", "https://www.yihu.com", "jzkl@jkzl.com"))
                .license("The Apache License, Version 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
                .build();
    }

    private ApiInfo privateAPIInfo() {
        return new ApiInfoBuilder()
                .title("医疗云平台API")
                .description("医疗云平台API，提供微服务监控接口")
                .version(API_VERSION)
                .termsOfServiceUrl("https://www.yihu.com")
                .contact(new Contact("Jkzl Xiamen R & D Center Platform Development.", "https://www.yihu.com", "jzkl@jkzl.com"))
                .license("The Apache License, Version 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
                .build();

    }

    private ApiInfo legacyApiInfo() {
        return new ApiInfoBuilder()
                .title("医疗云平台API")
                .description("医疗云平台API，提供遗留版本后端基础数据接口")
                .version(API_VERSION)
                .termsOfServiceUrl("https://www.yihu.com")
                .contact(new Contact("Jkzl Xiamen R & D Center Platform Development.", "https://www.yihu.com", "jzkl@jkzl.com"))
                .license("The Apache License, Version 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
                .build();

    }

}