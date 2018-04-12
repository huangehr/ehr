package com.yihu.ehr.fastdfs;

import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by szx on 2015/9/19.
 */
public class FastDFSClientPool {

    @Value("${fast-dfs.pool.init-size}")
    private int initPoolSize;
    @Value("${fast-dfs.pool.max-size}")
    private int maxPoolSize;
    private List<StorageClient> clientPool;
    private TrackerServer trackerServer;
//    private final List<TrackerServer> trackerServerList = new ArrayList<>();

    @PostConstruct
    private void init() {
        if (null == clientPool) {
            clientPool = new ArrayList<>();
        }
        try {
            synchronized (clientPool) {
                while (clientPool.size() < initPoolSize) {
                    clientPool.add(new StorageClient(getTrackerServer(), null));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (clientPool.isEmpty()) {
            throw new RuntimeException("FastDFS连接池初始化失败，请检查相关配置");
        }
    }

    @PreDestroy
    private void destroy() throws IOException{
        if (trackerServer != null) {
            trackerServer.close();
        }
        /*for (TrackerServer trackerServer : trackerServerList) {
            if (trackerServer != null) {
                trackerServer.close();
            }
        }*/
    }

    public synchronized TrackerServer getTrackerServer() throws IOException {
        if (null == trackerServer) {
            TrackerClient tracker = new TrackerClient();
            trackerServer = tracker.getConnection();
        }
        return trackerServer;
    }

    public synchronized StorageClient getStorageClient() throws IOException {
        if (clientPool.isEmpty()) {
            init();
        }
        int last_index = clientPool.size() - 1;
        StorageClient storageClient = clientPool.get(last_index);
        clientPool.remove(last_index);
        return storageClient;
    }

    public synchronized void releaseStorageClient(StorageClient client) {
        if (clientPool.size() > maxPoolSize) {
            if (client != null) {
                client = null;
            }
        } else {
            clientPool.add(client);
        }
    }

}
