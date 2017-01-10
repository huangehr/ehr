package com.yihu.ehr.web;

import com.yihu.ehr.constants.ArchiveStatus;
import com.yihu.ehr.constants.JobType;
import org.springframework.beans.factory.config.CustomEditorConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.beans.PropertyEditor;
import java.util.HashMap;
import java.util.Map;

/**
 * 将URL参数中的枚举值与JAVA的枚举值互相转换。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.12.20 15:40
 */
@Configuration
public class EnumerationConverterConfig {
    @Bean
    org.springframework.beans.factory.config.CustomEditorConfigurer customEditorConfigurer(){
        Map<Class<?>, Class<? extends PropertyEditor>> propertyEditorMap = new HashMap<>();
        propertyEditorMap.put(JobType.class, JobTypeConverter.class);
        propertyEditorMap.put(ArchiveStatus.class, ArchiveStatusConverter.class);

        CustomEditorConfigurer customEditorConfigurer = new CustomEditorConfigurer();
        customEditorConfigurer.setCustomEditors(propertyEditorMap);

        return customEditorConfigurer;
    }
}
