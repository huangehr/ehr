package com.yihu.ehr.fastdfs;

import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
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

    @PostConstruct
    private void init() {
        if (null == clientPool) {
            clientPool = new ArrayList<>();
        }
        try {
            synchronized (clientPool) {
                while (clientPool.size() < initPoolSize) {
                    clientPool.add(getNewStorageClient());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TrackerServer getTrackerServer() throws IOException {
        TrackerClient tracker = new TrackerClient();
        return tracker.getConnection();
    }

    private StorageClient getNewStorageClient() throws IOException {
        TrackerClient tracker = new TrackerClient();
        TrackerServer trackerServer = tracker.getConnection();
        StorageClient client = new StorageClient(trackerServer, null);
        return client;
    }

    public synchronized StorageClient getStorageClient() throws IOException {
        int last_index = clientPool.size() - 1;
        StorageClient transportClient = clientPool.get(last_index);
        clientPool.remove(last_index);
        if (clientPool.isEmpty()) {
            init();
        }
        return transportClient;
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
