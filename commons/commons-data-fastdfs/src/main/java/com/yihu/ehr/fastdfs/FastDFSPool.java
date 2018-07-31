package com.yihu.ehr.fastdfs;

import com.yihu.ehr.fastdfs.config.FastDFSConfig;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

/**
 * Created by szx on 2015/9/19.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class FastDFSPool {

    private Map<Integer, TrackerServer> trackerServerMap = new HashMap<>();
    private List<StorageClient> storageClientPool = new ArrayList<>();

    @Autowired
    private FastDFSConfig fastDFSConfig;

    public synchronized StorageClient getStorageClient() throws IOException {
        //ProtoCommon.activeTest(socket);
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
            if (storageClientPool.size() > fastDFSConfig.getPool().getMaxSize()) {
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
