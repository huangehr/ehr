package com.yihu.ehr.config;

import com.yihu.ehr.data.HBaseClient;
import com.yihu.ehr.util.log.LogService;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.security.UserGroupInformation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.hadoop.hbase.HbaseTemplate;

import java.io.IOException;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.11.28 16:26
 */

@Configuration
public class HbaseConfig{
    @Value("${hadoop.security.enable-kerberos}")
    private boolean enableKerberos;

    @Value("${kerberos.key-table}")
    private String keyTable;

    @Value("${kerberos.user-name}")
    private String user;

    @Value("${kerberos.config-file}")
    private String configFile;

    @Bean
    public org.apache.hadoop.conf.Configuration configuration() {
        return HBaseConfiguration.create();
    }

    @Bean
    public HbaseTemplate hbaseTemplate(org.apache.hadoop.conf.Configuration configuration){
        HbaseTemplate hbaseTemplate = new HbaseTemplate();
        try {
            hbaseTemplate.setConfiguration(configuration);
            if (enableKerberos) {
                configuration.set("hadoop.security.authentication", "Kerberos");

                ClassLoader loader = Thread.currentThread().getContextClassLoader();
                String tempKrbConf = loader.getResource(configFile).getFile();
                String tempKeyTable = loader.getResource(keyTable).getFile();

                System.setProperty("java.security.krb5.conf", tempKrbConf);
                UserGroupInformation.loginUserFromKeytab(user, tempKeyTable);
            }

            UserGroupInformation.setConfiguration(hbaseTemplate.getConfiguration());
        } catch (IOException ex) {
            LogService.getLogger(HBaseClient.class).error(ex.getMessage());
        }

        return hbaseTemplate;
    }
}
