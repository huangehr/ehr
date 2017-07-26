package com.yihu.quota.etl.extract;

import com.yihu.quota.etl.model.EsConfig;
import com.yihu.quota.etl.save.es.ElasticFactory;
import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by janseny on 2017/7/24.
 */
@Component
public class EsClientUtil {

    @Autowired
    private ElasticFactory elasticFactory;
    private Client client;
    private String host;//地址
    private Integer port;//端口
    private String index;// 索引 es相当与数据库
    private String type;// 类型 es 相当于表
    private String clusterName;//es clusterName

    public void getConfig(EsConfig esConfig){
        this.host = esConfig.getHost();
        this.port = esConfig.getPort();
        this.index = esConfig.getIndex();
        this.type = esConfig.getType();
        this.clusterName = esConfig.getClusterName();
        this.client = elasticFactory.getClient(host, 9300, clusterName);
    }


    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }
}
