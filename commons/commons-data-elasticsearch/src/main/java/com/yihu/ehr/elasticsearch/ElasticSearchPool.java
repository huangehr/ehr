package com.yihu.ehr.elasticsearch;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ElasticSearchDruidDataSourceFactory;
import org.elasticsearch.client.transport.NoNodeAvailableException;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by progr1mmer on 2018/1/4.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ElasticSearchPool {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchPool.class);

    @Autowired
    private ElasticSearchConfig elasticSearchConfig;
    @Value("${elasticsearch.pool.init-size:1}")
    private int initSize;
    @Value("${elasticsearch.pool.max-size:5}")
    private int maxSize;
    private List<TransportClient> clientPool;

    @PostConstruct
    private void init() {
        if (null == clientPool) {
            clientPool = new ArrayList<>();
        }
        synchronized (clientPool) {
            try {
                if (initSize < 1) {
                    initSize = 1;
                }
                while (clientPool.size() < initSize) {
                    TransportClient transportClient = getTransportClient();
                    clientPool.add(transportClient);
                }
                logger.info("[Init ElasticSearch Pool Size " + initSize + "]");
            } catch (Exception e){
                logger.error("[" + e.getMessage() + "]");
            }
        }
    }

    private TransportClient getTransportClient() {
        Settings settings = Settings.builder()
                .put("cluster.name", elasticSearchConfig.getClusterName())
                .put("client.transport.sniff", false)
                .build();
        String[] nodeArr = elasticSearchConfig.getClusterNodes().split(",");
        InetSocketTransportAddress[] socketArr = new InetSocketTransportAddress[nodeArr.length];
        for (int i = 0; i < socketArr.length; i++) {
            if (!StringUtils.isEmpty(nodeArr[i])) {
                String[] nodeInfo = nodeArr[i].split(":");
                socketArr[i] = new InetSocketTransportAddress(new InetSocketAddress(nodeInfo[0], new Integer(nodeInfo[1])));
            }
        }
        return TransportClient.builder().settings(settings).build().addTransportAddresses(socketArr);
    }

    @PreDestroy
    private void destroy(){
        if (clientPool != null) {
            for (TransportClient transportClient : clientPool) {
                transportClient.close();
            }
        }
    }

    public synchronized TransportClient getClient() {
        if (clientPool.isEmpty()) {
            init();
        }
        int last_index = clientPool.size() - 1;
        TransportClient transportClient= clientPool.remove(last_index);

        if(transportClient!=null){
            try{
                //update by cws 20180504
                //判断是否有用，能否取到服务器的信息
                transportClient.listedNodes();
            }catch (Exception e){
                if( e instanceof NoNodeAvailableException){
                    //判断是否报以上错误，或是即为取不到服务
                    //则重新返回一个新的链接。
                    return getTransportClient();
                }
            }
        }
        return transportClient;
    }

    public synchronized void releaseClient(TransportClient transportClient) {
        if (transportClient != null) {
            if (clientPool.size() > maxSize) {
                transportClient.close();
            } else {
                clientPool.add(0, transportClient);
            }
        }
    }

    public DruidDataSource getDruidDataSource() throws Exception {
        Properties properties = new Properties();
        properties.put("url", "jdbc:elasticsearch://" + elasticSearchConfig.getClusterNodes() + "/");
        DruidDataSource druidDataSource = (DruidDataSource) ElasticSearchDruidDataSourceFactory
                .createDataSource(properties);
        druidDataSource.setInitialSize(1);
        return druidDataSource;
    }

}
