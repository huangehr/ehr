package com.yihu.ehr.hbase.config;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.hadoop.hbase.HbaseTemplate;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.11.28 16:26
 * @modified by Progr1mmer 2017/11/23 注释启动连接代码
 */
@Configuration
@ConfigurationProperties(prefix = "hadoop")
public class HbaseConfig{

    private Map<String, String> hbaseProperties = new HashMap<>();
    private User user = new User();

    public Map<String, String> getHbaseProperties(){
        return this.hbaseProperties;
    }

    public void setHbaseProperties(Map<String, String> hbaseProperties) {
        this.hbaseProperties = hbaseProperties;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public class User {

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @PostConstruct
    private void configInfo() {
        StringBuilder info = new StringBuilder("{");
        hbaseProperties.forEach((key, val) -> info.append("\n  hadoop." + key + " = " + val));
        info.append("\n  hadoop.user.name = " + user.getName());
        info.append("\n}");
        System.out.println("Hbase.configInfo : " + info.toString());
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
        System.setProperty("HADOOP_USER_NAME", user.getName() != null ? user.getName() : "root");
        HbaseTemplate hbaseTemplate = new HbaseTemplate();
        hbaseTemplate.setConfiguration(configuration);
        return hbaseTemplate;
    }

}
