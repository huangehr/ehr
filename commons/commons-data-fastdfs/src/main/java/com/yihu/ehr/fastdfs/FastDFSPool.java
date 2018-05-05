package com.yihu.ehr.fastdfs;

import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.*;

/**
 * Created by szx on 2015/9/19.
 */
public class FastDFSPool {

    @Value("${fast-dfs.pool.init-size:1}")
    private int initPoolSize;
    @Value("${fast-dfs.pool.max-size:5}")
    private int maxPoolSize;
    private Map<Integer, TrackerServer> trackerServerMap = new HashMap<>();
    private List<StorageClient> storageClientPool = new ArrayList<>();

    @PreDestroy
    private void destroy() throws IOException {
        if (trackerServerMap != null) {
            for (int key : trackerServerMap.keySet()) {
                TrackerServer trackerServer = trackerServerMap.get(key);
                if (trackerServer != null) {
                    trackerServer.close();
                }
            }
        }
    }

    public synchronized StorageClient getStorageClient() throws IOException {
        if (storageClientPool.isEmpty()) {
            TrackerClient tracker = new TrackerClient();
            TrackerServer trackerServer = tracker.getConnection();
            StorageClient storageClient = new StorageClient(trackerServer, null);
            trackerServerMap.put(storageClient.hashCode(), trackerServer);
            return storageClient;
        }
        int last_index = storageClientPool.size() - 1;
        return storageClientPool.remove(last_index);
    }

    public synchronized void releaseStorageClient(StorageClient storageClient) throws IOException {
        if (storageClient != null) {
            if (storageClientPool.size() > maxPoolSize) {
                TrackerServer trackerServer = trackerServerMap.remove(storageClient.hashCode());
                if (trackerServer != null) {
                    trackerServer.close();
                }
            } else {
                storageClientPool.add(0, storageClient);
            }
        }
    }
}
