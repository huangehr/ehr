package com.yihu.ehr.ui;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

//@Configuration
@EnableSwagger2
@ComponentScan("com.yihu.ehr.*.controller")
public class SwaggerConfig extends WebMvcConfigurerAdapter {
    public static final String PUBLIC_API = "Public";
    public static final String ADMIN_API = "Admin";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Bean
    public Docket publicAPI(){
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
    public Docket adminAPI(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName(ADMIN_API)
                .genericModelSubstitutes(DeferredResult.class)
                .useDefaultResponseMessages(false)
                .forCodeGeneration(false)
                .pathMapping("/")
                .select()
                .paths(or(regex("/rest/\\{api_version.*")))
                .build()
                .apiInfo(adminApiInfo());
    }

    private ApiInfo publicApiInfo() {
        ApiInfo apiInfo = new ApiInfo("健康档案平台API",
                "健康档案平台开放API，提供健康档案服务。",
                "1.0",
                "NO terms of service",
                "wenfujian@jkzl.com",
                "The Apache License, Version 2.0",
                "http://www.apache.org/licenses/LICENSE-2.0.html"
        );

        return apiInfo;
    }

    private ApiInfo adminApiInfo() {
        ApiInfo apiInfo = new ApiInfo("健康档案平台API",
                "健康档案平台管理API，平台管理服务。",
                "1.0",
                "NO terms of service",
                "wenfujian@jkzl.com",
                "The Apache License, Version 2.0",
                "http://www.apache.org/licenses/LICENSE-2.0.html"
        );

        return apiInfo;
    }
}