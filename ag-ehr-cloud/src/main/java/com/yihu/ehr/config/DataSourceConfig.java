package com.yihu.ehr.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * Created by Administrator on 2017/2/21.
 */
@Configuration
public class DataSourceConfig {
    /**
     * 主数据源
     *
     * @return
     */
    @Bean
    @Primary//主库 默认不写名字用这个
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource primaryReadWriteDataSource() {
        return DataSourceBuilder.create().build();
    }

}
