package com.yihu.ehr.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.yihu.ehr.util.compress.Zipper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 常用工具类Bean配置源，可用来产生一些常用的工具单件，并由Spring管理。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.12.29 18:08
 */
@Configuration
public class UtilConfiguration {

    /**
     * 基于Jackson的对象映射工具，将对象与JSON之间进行转换。
     *
     * http://wiki.fasterxml.com/JacksonBestPracticesPerformance
     *
     * @return
     */
    @Bean
    ObjectMapper objectMapper(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_WITH_ZONE_ID, true);

        return objectMapper;
    }

    /**
     * Zipper 单件。
     *
     * @return
     */
    @Bean
    Zipper zipper(){
        return new Zipper();
    }
}
