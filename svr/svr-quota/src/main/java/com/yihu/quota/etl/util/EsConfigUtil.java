package com.yihu.quota.etl.util;

import org.springframework.stereotype.Component;

/**
 * Created by janseny on 2017/7/24.
 */
@Component
public class EsConfigUtil {

    private String host;//地址
    private Integer port;//端口
    private String index;// 索引 es相当与数据库
    private String type;// 类型 es 相当于表
    private String clusterName;//es clusterName

    public void getConfig(String host,int port,String index,String type,String clusterName){
        this.host = host;
        this.port = port;
        this.index = index;
        this.type = type;
        this.clusterName = clusterName;
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
