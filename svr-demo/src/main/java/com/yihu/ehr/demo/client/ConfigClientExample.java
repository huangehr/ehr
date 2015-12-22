package com.yihu.ehr.demo.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * 配置客户端选项。使用URI引用Spring Config Server服务，可在此服务启动时，使用属性占位符来初始化环境变量。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.11.17 15:13
 */
@Configuration
public class ConfigClientExample {
    @Value("${hadoop.security.enable-kerberos}")
    public String url;

    ConfigClientExample(){

    }

    @PostConstruct
    public void init(){
        System.out.println("MySQL arguments initialized");
        System.out.println("Url: " + url);
    }
}
