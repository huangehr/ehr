package com.yihu.ehr.fastdfs;

import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by szx on 2015/9/19.
 */
public class FastDFSClientPool {
    private int maxPoolSize;

    private Map<StorageClient, Boolean> map = new HashMap<>();

    public void setMaxPoolSize(int poolSize){
        this.maxPoolSize = poolSize;
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
        StorageClient client = null;
        for (Entry<StorageClient, Boolean> entry : map.entrySet()) {
            if (entry.getValue()) {
                client = entry.getKey();
                map.put(client, false);
                break;
            }
        }
        if (client == null) {
            if (map.size() < maxPoolSize) {
                client = getNewStorageClient();
                map.put(client, false);
            }
        }

        return client;
    }

    public void releaseStorageClient(StorageClient client) {
        if (client == null) return;

        if (map.containsKey(client)) {
            map.put(client, true);
        } else {
            client = null;
        }
    }
}
