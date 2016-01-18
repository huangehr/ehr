package com.yihu.ehr.config;

import com.yihu.ehr.data.SolrClientFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Solr配置，用于创建Solr工厂对象。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.12.01 20:55
 */
@Configuration
public class SolrConfig {
    @Value("${solr.zkClientTimeout}")
    int zkClientTimeout;

    @Value("${solr.zkConnectTimeout}")
    int zkConnectTimeout;

    @Value("${solr.zookeeper}")
    String solrServer;

    @Bean
    SolrClientFactory clientFactory(){
        SolrClientFactory solrClientFactory = new SolrClientFactory();
        solrClientFactory.setSolrServer(solrServer);
        solrClientFactory.setZkClientTimeout(zkClientTimeout);
        solrClientFactory.setZkConnectTimeout(zkConnectTimeout);

        return solrClientFactory;
    }
}
