package com.yihu.ehr.solr;

import com.yihu.ehr.solr.config.SolrConfig;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.solr.server.support.MulticoreSolrClientFactory;
import org.springframework.stereotype.Service;

/**
 * Solr连接池
 * @author hzp
 * @version 1.0
 * @created 2016.04.26
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SolrPool {

    @Autowired
    private SolrConfig solrConfig;

    private volatile MulticoreSolrClientFactory factory;

    protected MulticoreSolrClientFactory getFactory(){
        if (factory != null) {
            return factory;
        }
        synchronized (MulticoreSolrClientFactory.class) {
            if (null == factory) {
                CloudSolrClient client = new CloudSolrClient(solrConfig.getZkHost());
                factory = new MulticoreSolrClientFactory(client);
            }
            return factory;
        }
    }

    public SolrClient getConnection(String core) throws Exception{
        if (factory != null) {
            return factory.getSolrClient(core);
        }
        return getFactory().getSolrClient(core);
    }

}
