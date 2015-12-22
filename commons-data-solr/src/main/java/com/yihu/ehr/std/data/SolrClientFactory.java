package com.yihu.ehr.std.data;

import com.yihu.ehr.util.log.LogService;
import org.apache.solr.client.solrj.impl.CloudSolrClient;

/**
 * Solr客户端, 可获取单机或群集Solr客户端对象.
 */
public class SolrClientFactory {
    private int zkClientTimeout;
    private int zkConnectTimeout;
    private String solrServer;

    public void setZkClientTimeout(int zkClientTimeout) {
        this.zkClientTimeout = zkClientTimeout;
    }

    public void setZkConnectTimeout(int zkConnectTimeout) {
        this.zkConnectTimeout = zkConnectTimeout;
    }

    public void setSolrServer(String solrServer) {
        this.solrServer = solrServer;
    }

    public synchronized CloudSolrClient getCloudSolrClient(String coreName) {
        CloudSolrClient cloudSolrClient = null;
        try {
            cloudSolrClient = new CloudSolrClient(solrServer);
            cloudSolrClient.setDefaultCollection(coreName);
            cloudSolrClient.setZkClientTimeout(zkClientTimeout);
            cloudSolrClient.setZkConnectTimeout(zkConnectTimeout);
            cloudSolrClient.connect();
        } catch (Exception ex) {
            LogService.getLogger(SolrClientFactory.class).error("获取Solr群集客户端失败: " + ex.getMessage());
        }

        return cloudSolrClient;
    }
}
