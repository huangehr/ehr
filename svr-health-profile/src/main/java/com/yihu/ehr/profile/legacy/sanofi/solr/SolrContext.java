package com.yihu.ehr.profile.legacy.sanofi.solr;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.solr.core.SolrTemplate;

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
    public SolrClient solrClient() {
        return new CloudSolrClient(zkHost);
    }

    @Bean
    public SolrTemplate solrTemplate(SolrClient solrClient) throws Exception {
        return new SolrTemplate(solrClient);
    }
}
