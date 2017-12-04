package com.yihu.ehr.dfs.es.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by progr1mmer on 2017/12/1.
 */

@ConfigurationProperties(prefix = "elasticsearch")
@Configuration
public class ElasticSearchConfig {

    // 集群名称
    private String clusterName;
    // 节点
    private String clusterNodes;

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getClusterNodes() {
        return clusterNodes;
    }

    public void setClusterNodes(String clusterNodes) {
        this.clusterNodes = clusterNodes;
    }
}
