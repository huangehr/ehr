package com.yihu.ehr.fastdfs.config;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.TrackerGroup;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.11.27 16:08
 * Modified by progr1mmer on 2018/07/26.
 */
@Configuration
@ConfigurationProperties(prefix = "fast-dfs")
public class FastDFSConfig {

    private int connectTimeout;
    private int networkTimeout;
    private String charset;
    private String trackerServer;
    private String publicServer;
    private Pool pool = new Pool();
    private Http http = new Http();

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getNetworkTimeout() {
        return networkTimeout;
    }

    public void setNetworkTimeout(int networkTimeout) {
        this.networkTimeout = networkTimeout;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getTrackerServer() {
        return trackerServer;
    }

    public void setTrackerServer(String trackerServer) {
        this.trackerServer = trackerServer;
    }

    public String getPublicServer() {
        return publicServer;
    }

    public void setPublicServer(String publicServer) {
        this.publicServer = publicServer;
    }

    public Pool getPool() {
        return pool;
    }

    public void setPool(Pool pool) {
        this.pool = pool;
    }

    public Http getHttp() {
        return http;
    }

    public void setHttp(Http http) {
        this.http = http;
    }

    public class Pool {

        private int initSize;
        private int maxSize;
        private int waitTime;

        public int getInitSize() {
            return initSize;
        }

        public void setInitSize(int initSize) {
            this.initSize = initSize;
        }

        public int getMaxSize() {
            return maxSize;
        }

        public void setMaxSize(int maxSize) {
            this.maxSize = maxSize;
        }

        public int getWaitTime() {
            return waitTime;
        }

        public void setWaitTime(int waitTime) {
            this.waitTime = waitTime;
        }
    }

    public class Http {

        private int trackerHttpPort;
        private boolean antiStealToken;
        private String secretKey;

        public int getTrackerHttpPort() {
            return trackerHttpPort;
        }

        public void setTrackerHttpPort(int trackerHttpPort) {
            this.trackerHttpPort = trackerHttpPort;
        }

        public boolean isAntiStealToken() {
            return antiStealToken;
        }

        public void setAntiStealToken(boolean antiStealToken) {
            this.antiStealToken = antiStealToken;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }
    }

    @PostConstruct
    void init() throws Exception{
        // 此代码复制自：ClientGlobal.init() 方法
        ClientGlobal.g_connect_timeout = connectTimeout;
        if (ClientGlobal.g_connect_timeout < 0) {
            ClientGlobal.g_connect_timeout = 5;
        }
        ClientGlobal.g_connect_timeout *= 1000;

        ClientGlobal.g_network_timeout = networkTimeout;
        if (ClientGlobal.g_network_timeout < 0) {
            ClientGlobal.g_network_timeout = 30;
        }
        ClientGlobal.g_network_timeout *= 1000;

        ClientGlobal.g_charset = charset;
        if (ClientGlobal.g_charset == null || ClientGlobal.g_charset.length() == 0) {
            ClientGlobal.g_charset = "ISO8859-1";
        }

        String[] szTrackerServers = trackerServer.split(",");
        if (szTrackerServers == null) {
            throw new MyException("item \"tracker_server\" not found");
        } else {
            InetSocketAddress[] tracker_servers = new InetSocketAddress[szTrackerServers.length];

            for (int i = 0; i < szTrackerServers.length; ++i) {
                String[] parts = szTrackerServers[i].split("\\:", 2);
                if (parts.length != 2) {
                    throw new MyException("the value of item \"tracker_server\" is invalid, the correct format is host:port");
                }

                tracker_servers[i] = new InetSocketAddress(parts[0].trim(), Integer.parseInt(parts[1].trim()));
            }

            ClientGlobal.g_tracker_group = new TrackerGroup(tracker_servers);
            ClientGlobal.g_tracker_http_port = http.trackerHttpPort;
            ClientGlobal.g_anti_steal_token = http.antiStealToken;
            if (ClientGlobal.g_anti_steal_token) {
                ClientGlobal.g_secret_key = http.secretKey;
            }
        }
        System.out.println("FastDFS.configInfo(): " + ClientGlobal.configInfo());
    }
}
