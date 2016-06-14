package com.yihu.ehr.data.hbase.config;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.hadoop.hbase.HbaseTemplate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.11.28 16:26
 */
@Configuration
@ConfigurationProperties(prefix = "hadoop")
public class HbaseConfig{
    private Map<String, String> hbaseProperties = new HashMap<>();

    public Map<String, String> getHbaseProperties(){
        return this.hbaseProperties;
    }

    @Bean
    public org.apache.hadoop.conf.Configuration configuration() {
        Set<String> keys = new HashSet<>(hbaseProperties.keySet());
        for (String key : keys){
            String value = hbaseProperties.remove(key);
            key = key.replaceAll("^\\d{1,2}\\.", "");

            hbaseProperties.put(key, value);
        }

        org.apache.hadoop.conf.Configuration configuration = HBaseConfiguration.create();
        hbaseProperties.keySet().stream().filter(key -> hbaseProperties.get(key) != null).forEach(key -> {
            configuration.set(key, hbaseProperties.get(key));
        });

        return configuration;
    }

    @Bean
    public HbaseTemplate hbaseTemplate(org.apache.hadoop.conf.Configuration configuration){
        HbaseTemplate hbaseTemplate = new HbaseTemplate();
        hbaseTemplate.setConfiguration(configuration);

        return hbaseTemplate;
    }
}
