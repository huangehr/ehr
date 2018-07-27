package com.yihu.ehr.elasticsearch;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ElasticSearchDruidDataSourceFactory;
import com.yihu.ehr.elasticsearch.config.ElasticSearchConfig;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.InetSocketAddress;
import java.util.Properties;

/**
 * Created by progr1mmer on 2018/1/4.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ElasticSearchPool {

    private static volatile TransportClient transportClient;

    @Autowired
    private ElasticSearchConfig elasticSearchConfig;

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

    /**
     * 1.TransportClient本身支持多线程的数据请求
     * 2.移除多个TransportClient的线程池支持，减少Socket链接
     * 3.基于多重检查的单例模式，兼顾安全和效率
     * 4.使用完毕后无需进行关闭操作
     * @return
     */
    public TransportClient getClient() {
        if (transportClient != null) {
            if (transportClient.connectedNodes().isEmpty()) {
                synchronized (TransportClient.class) {
                    if (transportClient.connectedNodes().isEmpty()) {
                        transportClient = getTransportClient();
                    }
                }
            }
            return transportClient;
        }
        synchronized (TransportClient.class) {
            if (null == transportClient) {
                transportClient = getTransportClient();
            }
        }
        return transportClient;
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
