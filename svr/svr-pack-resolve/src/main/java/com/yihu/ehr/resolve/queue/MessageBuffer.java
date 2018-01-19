package com.yihu.ehr.resolve.queue;

import com.yihu.ehr.constants.ArchiveStatus;
import com.yihu.ehr.resolve.feign.PackageMgrClient;
import com.yihu.ehr.model.packs.MPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.04.01 8:37
 */
//@Component
@Deprecated
public class MessageBuffer {

    private Queue<Object> messages = new LinkedList<>();
    private long timeThreshold = new Date().getTime();

    @Autowired
    private PackageMgrClient packageMgrClient;

    public int size() {
        return messages.size();
    }

    public synchronized void putMessage(Object message){
        messages.add(message);
    }

    public synchronized <T> T getMessage() {
        if(messages.isEmpty()){
            String filters = "archiveStatus=Received";
            String sorts = "-receiveDate";
            int count = 500;
            long dateCheck = new Date().getTime();
            if(dateCheck - timeThreshold > 60000){
                timeThreshold = new Date().getTime();
                packageMgrClient.sendResolveMessage(filters, sorts, count);
            }
            return null;
        }
        MPackage pack = (MPackage)messages.remove();
        packageMgrClient.reportStatus(pack.getId(), ArchiveStatus.Acquired, "正在入库中");
        return (T)pack;
    }
}
