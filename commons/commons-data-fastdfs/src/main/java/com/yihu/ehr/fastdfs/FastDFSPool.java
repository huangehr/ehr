package com.yihu.ehr.fastdfs;

import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by szx on 2015/9/19.
 */
public class FastDFSPool {

    private static final Logger logger = LoggerFactory.getLogger(FastDFSPool.class);

    @Value("${fast-dfs.pool.init-size}")
    private int initPoolSize;
    @Value("${fast-dfs.pool.max-size}")
    private int maxPoolSize;
    private List<TrackerServer> trackerServerPool;

    @PostConstruct
    private void init() {
        if (null == trackerServerPool) {
            trackerServerPool = new ArrayList<>();
        }
        try {
            synchronized (trackerServerPool) {
                logger.info("Init fastDfs pool");
                while (trackerServerPool.size() < initPoolSize) {
                    TrackerClient tracker = new TrackerClient();
                    TrackerServer trackerServer = tracker.getConnection();
                    logger.info("[Host:" + trackerServer.getInetSocketAddress().getAddress().getHostName() + "]");
                    trackerServerPool.add(trackerServer);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (trackerServerPool.isEmpty()) {
            throw new RuntimeException("FastDFS连接池初始化失败，请检查相关配置");
        }
    }

    @PreDestroy
    private void destroy() throws IOException{
        for (TrackerServer trackerServer : trackerServerPool) {
            if (trackerServer != null) {
                trackerServer.close();
            }
        }
    }

    public synchronized TrackerServer getTrackerServer() throws IOException {
        if (trackerServerPool.isEmpty()) {
            init();
        }
        int last_index = trackerServerPool.size() - 1;
        TrackerServer trackerServer = trackerServerPool.get(last_index);
        trackerServerPool.remove(last_index);
        return trackerServer;
    }

    public synchronized void releaseTrackerServer(TrackerServer trackerServer) throws IOException {
        if (trackerServerPool.size() > maxPoolSize) {
            if (trackerServer != null) {
                trackerServer.close();
            }
        } else {
            trackerServerPool.add(trackerServer);
        }
    }

}
