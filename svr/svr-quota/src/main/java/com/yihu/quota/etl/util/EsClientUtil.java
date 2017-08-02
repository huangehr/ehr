package com.yihu.quota.etl.util;



import com.yihu.quota.etl.extract.es.EsExtract;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private Settings setting;

    private Map<String, Client> clientMap = new ConcurrentHashMap<String, Client>();

    private Map<String, Integer> ips = new HashMap<String, Integer>(); // hostname port

    private String ip = "172.17.110.17";// 172.19.103.68
    private int port = 9300;
    private String clusterName = "elasticsearch";//jkzl

    private EsClientUtil() {
        init();
    }

    public static final EsClientUtil getInstance() {
        return ClientHolder.INSTANCE;
    }

    private static class ClientHolder {
        private static final EsClientUtil INSTANCE = new EsClientUtil();
    }

    /**
     * 初始化默认的client
     */
    public void init(){
        ips.put(ip, port);
        setting = Settings.settingsBuilder()
                .put("client.transport.sniff",true)
                .put("cluster.name",clusterName).build();
        addClient(setting, getAllAddress(ips));
    }

    /**
     * 添加新的client
     * @param newIp
     * @param newPort
     * @param newClusterName
     */
    public void addNewClient(String newIp,int newPort,String newClusterName){
        ips.put(newIp, port);
        setting = Settings.settingsBuilder()
                .put("client.transport.sniff",true)
                .put("cluster.name",newClusterName).build();
        addClient(setting, getAllAddress(ips));
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

    public Client getClient() {
        return getClient(clusterName);
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
