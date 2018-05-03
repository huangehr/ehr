package com.yihu.ehr.elasticsearch;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ElasticSearchDruidDataSourceFactory;
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

    @Value("${elasticsearch.pool.init-size:1}")
    private int initSize;
    @Value("${elasticsearch.pool.max-size:5}")
    private int maxSize;
    private List<TransportClient> clientPool;
    @Autowired
    private ElasticSearchConfig elasticSearchConfig;

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
                    TransportClient transportClient = TransportClient.builder().settings(settings).build().addTransportAddresses(socketArr);
                    clientPool.add(transportClient);
                }
                logger.info("[Init ElasticSearch Pool Size " + initSize + "]");
            } catch (Exception e){
                logger.error("[" + e.getMessage() + "]");
            }
        }
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
        //logger.info("ElasticSearch Pool Size " + clientPool.size());
        int last_index = clientPool.size() - 1;
        return clientPool.remove(last_index);
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
