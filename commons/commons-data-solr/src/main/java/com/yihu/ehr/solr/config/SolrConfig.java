package com.yihu.ehr.solr.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Created by progr1mmer on 2018/7/26.
 */
@Configuration
@ConfigurationProperties(prefix = "spring.data.solr")
public class SolrConfig {

    private String zkHost;

    public String getZkHost() {
        return zkHost;
    }

    public void setZkHost(String zkHost) {
        this.zkHost = zkHost;
    }

    @PostConstruct
    private void configInfo() {
        StringBuilder info = new StringBuilder("{");
        info.append("\n  spring.data.solr.zk-host = " + zkHost);
        info.append("\n}");
        System.out.println("Solr.configInfo : " + info.toString());
    }

}
