package com.yihu.quota.etl.save.es;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.ClientConfig;
import io.searchbox.client.config.HttpClientConfig;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * Created by chenweida on 2017/6/5.
 */
@Component
public class ElasticFactory {
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
                .discoveryEnabled(true)
                .readTimeout(30000)//30秒
                .build());
        return factory.getObject();
    }

    public Client getClient(String host, Integer port,String clusterName) {
        try {
            Settings settings = Settings.settingsBuilder()
                    .put("client.transport.sniff", true)
                    .put("cluster.name", StringUtils.isEmpty(clusterName)?"elasticsearch":clusterName)
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
