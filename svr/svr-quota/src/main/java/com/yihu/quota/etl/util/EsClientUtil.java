package com.yihu.quota.etl.util;



import com.yihu.quota.etl.extract.es.EsExtract;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    @Autowired
    EsConfigUtil esConfigUtil;
    private Settings setting;
    private Map<String, Client> clientMap = new ConcurrentHashMap<String, Client>();
    private Map<String, Integer> ips = new HashMap<String, Integer>(); // hostname port
    private int port = 9300;
    private String clusterName = "elasticsearch";//jkzl

    public static final EsClientUtil getInstance() {
        return ClientHolder.INSTANCE;
    }

    private static class ClientHolder {
        private static final EsClientUtil INSTANCE = new EsClientUtil();
    }

    /**
     * 添加新的client
     * @param ip
     * @param port
     * @param clusterName
     */
    public Client getClient(String ip,int port,String index,String type,String clusterName){
        ips.put(ip, 9300);
        setting = Settings.settingsBuilder()
                .put("client.transport.sniff",true)
                .put("cluster.name",clusterName).build();
        addClient(setting, getAllAddress(ips));
        esConfigUtil.getConfig(ip,port,index,type,clusterName);
        return getClient(clusterName);
    }



    /**
     * 获得所有的地址端口
     * @return
     */
    public List<InetSocketTransportAddress> getAllAddress(Map<String, Integer> ips) {
        List<InetSocketTransportAddress> addressList = new ArrayList<InetSocketTransportAddress>();
        try {
            for (String ip : ips.keySet()) {
                addressList.add(new InetSocketTransportAddress(InetAddress.getByName(ip), ips.get(ip)));
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
            logger.info("elasticSearch 无法识别的IP");
        }
        return addressList;
    }

    public Client getClient(String clusterName) {
        return clientMap.get(clusterName);
    }

    public void addClient(Settings setting, List<InetSocketTransportAddress> transportAddress) {
        Client client = TransportClient.builder().settings(setting).build()
                .addTransportAddresses(transportAddress
                        .toArray(new InetSocketTransportAddress[transportAddress.size()]));
        clientMap.put(setting.get("cluster.name"), client);
    }
}
