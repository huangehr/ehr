package com.yihu.ehr.hbase.config;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.hadoop.hbase.HbaseTemplate;

import java.util.*;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.11.28 16:26
 * @modify by Progr1mmer 2017/11/23 注释启动连接代码
 */
@Configuration
@ConfigurationProperties(prefix = "hadoop")
public class HbaseConfig{

    private Map<String, String> hbaseProperties = new HashMap<>();

    public Map<String, String> getHbaseProperties(){
        return this.hbaseProperties;
    }

    @Value("${hadoop.user.name}")
    private String user;

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
        System.setProperty("HADOOP_USER_NAME", user);
        HbaseTemplate hbaseTemplate = new HbaseTemplate();
        hbaseTemplate.setConfiguration(configuration);
        /**
        try{
            String tableName = "HealthProfile";
            Connection connection = ConnectionFactory.createConnection(configuration);
            Admin admin = connection.getAdmin();
            boolean ex = admin.tableExists(TableName.valueOf(tableName));
            //判断是否存在
            if(ex){
                hbaseTemplate.execute(tableName, new TableCallback<Object>() {
                    @Override
                    public Object doInTable(HTableInterface table) throws Throwable {
                        Get get = new Get(Bytes.toBytes("connection-init"));
                        Result result = table.get(get);

                        return result;
                    }
                });
            }
            admin.close();
            connection.close();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        */
        return hbaseTemplate;
    }
}
