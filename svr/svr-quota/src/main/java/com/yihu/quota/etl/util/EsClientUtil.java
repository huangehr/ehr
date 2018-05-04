package com.yihu.quota.etl.util;



import com.yihu.quota.etl.extract.es.EsExtract;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
* Created by janseny on 2017/8/1
 */
@Component
public class EsClientUtil {
    private Logger logger = LoggerFactory.getLogger(EsExtract.class);

    /**
     * @param host "localhost"
     * @param port 9200
     * @return
     */
    public JestClient getJestClient(String host, Integer port) {
        String hostAddress="http://"+host+":"+port;
        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig
                .Builder(hostAddress)
                .multiThreaded(true)
                //.discoveryEnabled(true)
                .readTimeout(30000)//30秒
                .build());
        return factory.getObject();
    }

    public Client getClient(String host, Integer port,String clusterName) {
        try {
            Settings settings = Settings.settingsBuilder()
                    .put("cluster.name", StringUtils.isEmpty(clusterName)?"elasticsearch":clusterName)
                    .put("client.transport.sniff", false)
                    .build();
            Client client = TransportClient.builder().settings(settings).build()
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));
            return client;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
