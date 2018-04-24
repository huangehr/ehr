package com.yihu.ehr.analyze.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author Airhead
 * @created 2018-01-18
 */
@Configuration
public class FastDfsConfig {
    @Value(value = "${fast-dfs.public-server}")
    private String publicServer;

    public String getPublicServer() {
        return publicServer;
    }
}
