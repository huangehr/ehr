package com.yihu.ehr.solr;

import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.support.SimpleSolrRepository;

/**
 * Solr配置。仅支持SolrCloud，不支持单核模式。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.04.18 18:47
 */
@Configuration
public class SolrContext {
    @Value("${spring.data.solr.zk-host}")
    String zkHost;

    @Bean
    public CloudSolrServer solrServer() {
        return new CloudSolrServer(zkHost);
    }
}
