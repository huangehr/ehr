package com.yihu.ehr.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.util.compress.Zipper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;

/**
 * 常用工具类Bean配置源，可用来产生一些常用的工具单件，并由Spring管理。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.12.29 18:08
 */
@Configuration
public class UtilitiesConfiguration {
    @Autowired
    ObjectMapper objectMapper;

    /**
     * 基于Jackson的对象映射工具，将对象与JSON之间进行转换。
     *
     * http://wiki.fasterxml.com/JacksonBestPracticesPerformance
     *
     * @return
     */
    @PostConstruct
    protected void objectMapperConfig() throws JsonProcessingException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'ZZ");
        objectMapper.setDateFormat(dateFormat);
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
