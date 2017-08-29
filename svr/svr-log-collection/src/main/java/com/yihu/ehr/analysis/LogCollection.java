package com.yihu.ehr.analysis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * Created by Administrator on 2017/2/6.
 */
@SpringBootApplication
public class LogCollection {
    public static ApplicationContext ctx = null;

    public static void main(String[] args) {
        ctx = SpringApplication.run(LogCollection.class, args);
    }
}

