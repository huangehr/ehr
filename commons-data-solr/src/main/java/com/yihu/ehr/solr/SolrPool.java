package com.yihu.ehr.solr;

import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Solr连接池
 * @author hzp
 * @version 1.0
 * @created 2016.04.26
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SolrPool {

    @Value("${data.solr.zk-host}")
    String zkHost;
    private int zkClientTimeout = 6000;
    private int zkConnectTimeout = 6000;
    private int maxPoolSize = 10;
    private int waitTime = 500;
    private Map<CloudSolrClient, Boolean> map = new HashMap<CloudSolrClient, Boolean>();
    private Map<String,Integer> coreMap = new HashMap<String,Integer>();;

    /**
     * 创建新连接实例
     */
    private CloudSolrClient getNewConnection(String core) throws Exception {
        System.out.println("创建新solr连接"+core+"[HOST:"+zkHost+"]");
        CloudSolrServer solr = new CloudSolrServer(zkHost);
        solr.setDefaultCollection(core);
        solr.setZkClientTimeout(zkClientTimeout);
        solr.setZkConnectTimeout(zkConnectTimeout);
        solr.connect();
        return solr;
    }

    /**
     * 连接数统计
     */
    private void removeCore(String core){
        if(core == null || core == "")return;
        if(coreMap.containsKey(core)) {
            int coreCount = coreMap.get(core);
            if(coreCount==1){
                coreMap.remove(core);
            }
            else
                coreMap.put(core,coreCount - 1);

        }
    }

    /**
     * 当前池中连接数
     * @param core
     * @return
     */
    private int getCoreCount(String core){
        if(core == null || core == "")return 0;

        if(coreMap.containsKey(core)) {
            return coreMap.get(core);
        }
        else
            return 0;
    }

    /**
     * 获取连接
     * @param core
     * @return
     * @throws Exception
     */
    public synchronized CloudSolrClient getConnection(String core) throws Exception{
        CloudSolrClient conn = null;
        for (Map.Entry<CloudSolrClient, Boolean> entry : map.entrySet()) {
            if (entry.getValue() && entry.getKey().getDefaultCollection().equals(core)) {
                conn = entry.getKey();
                map.put(conn, false);
                break;
            }
        }

        if (conn == null) {
            if (getCoreCount(core) < maxPoolSize) {
                conn = getNewConnection(core);
                map.put(conn, false);
                if(coreMap.containsKey(core)) {
                    int coreCount = coreMap.get(core);
                    coreMap.put(core,coreCount + 1);
                }
                else{
                    coreMap.put(core,1);
                }
            } else {
                wait(waitTime);
                conn = getConnection(core);
            }
        }
        return conn;
    }

    public void release(CloudSolrClient conn) throws Exception{
        if (conn == null) {
            return;
        }
        try {
            if(map.containsKey(conn)) {
                if (conn.getZkStateReader() == null) {
                    conn.close();
                    System.out.println("关闭solr连接");
                    map.remove(conn);
                    removeCore(conn.getDefaultCollection());
                }
                else {
                    System.out.println("solr连接放回池中");
                    map.put(conn, true);
                }
            } else {
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
