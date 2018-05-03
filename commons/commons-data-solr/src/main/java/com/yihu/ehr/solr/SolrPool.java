package com.yihu.ehr.solr;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.solr.server.support.MulticoreSolrClientFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;


/**
 * Solr连接池
 * @author hzp
 * @version 1.0
 * @created 2016.04.26
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SolrPool {

    @Value("${spring.data.solr.zk-host:localhost:2181}")
    private String zkHost;

    private MulticoreSolrClientFactory factory;

    protected synchronized MulticoreSolrClientFactory getFactory(){
        if (null == factory) {
            CloudSolrClient client = new CloudSolrClient(zkHost);
            factory = new MulticoreSolrClientFactory(client);
        }
        return factory;
    }

    public SolrClient getConnection(String core) throws Exception{
        if (factory != null) {
            return factory.getSolrClient(core);
        }
        return getFactory().getSolrClient(core);
    }

    @PreDestroy
    private void destroy() {
        if (factory != null) {
            factory.destroy();
        }
    }

    /**
     * 关闭连接
    public void close(SolrClient solrClient) throws Exception{
        solrClient.close();
    }
     */


}
