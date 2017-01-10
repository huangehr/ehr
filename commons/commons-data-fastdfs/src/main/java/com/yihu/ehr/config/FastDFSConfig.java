package com.yihu.ehr.config;

import com.yihu.ehr.fastdfs.FastDFSClientPool;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.util.log.LogService;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.TrackerGroup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.11.27 16:08
 */
@Configuration
public class FastDFSConfig {
    @Value("${fast-dfs.pool.init-size}")
    private int initPoolSize;

    @Value("${fast-dfs.pool.max-size}")
    private int maxPoolSize;

    @Value("${fast-dfs.pool.wait-time}")
    private int waitTime;

    @Value("${fast-dfs.connect-timeout}")
    private int connectTimeout;

    @Value("${fast-dfs.network-timeout}")
    private int networkTimeout;

    @Value("${fast-dfs.charset}")
    private String charset;

    @Value("${fast-dfs.tracker-server}")
    private String trackerServers;

    @Value("${fast-dfs.http.tracker-http-port}")
    private int httpPort;

    @Value("${fast-dfs.http.anti-steal-token}")
    private boolean antiStealToken;

    @Value("${fast-dfs.http.secret-key}")
    private String secretKey;

    @PostConstruct
    void init() {
        try {
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

            String[] szTrackerServers = trackerServers.split(";");
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
                ClientGlobal.g_tracker_http_port = httpPort;
                ClientGlobal.g_anti_steal_token = antiStealToken;
                if (ClientGlobal.g_anti_steal_token) {
                    ClientGlobal.g_secret_key = secretKey;
                }

            }
        } catch (MyException e) {
            LogService.getLogger(FastDFSUtil.class).error("FastDFS初始化失败: " + e.getMessage());
        }
    }

    @Bean
    public FastDFSClientPool fastDFSClientPool(){
        FastDFSClientPool clientPool = new FastDFSClientPool();
        clientPool.setMaxPoolSize(maxPoolSize);

        return clientPool;
    }

    @Bean
    public FastDFSUtil fastDFSUtil(){
        FastDFSUtil util = new FastDFSUtil();
        return util;
    }
}
