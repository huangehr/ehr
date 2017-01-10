package com.yihu.ehr.queue;

import com.yihu.ehr.feign.XPackageMgrClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.04.01 8:37
 */
@Component
public class MessageBuffer {
    Queue<Object> messages = new LinkedList<>();

    @Autowired
    XPackageMgrClient packageMgrClient;

    long timeThreshold = new Date().getTime();

    public synchronized void putMessage(Object message){
        messages.add(message);
    }


    public synchronized <T> T getMessage() {
        System.out.println("--------------begin------------------");
        String filters = "archiveStatus=Received";
        String sorts = "-receiveDate";
        int count = 500;
        if(messages.isEmpty()){
            long dateCheck = new Date().getTime();
            System.out.println("--------------timeThreshold："+timeThreshold+"---------------");
            System.out.println("--------------current ："+dateCheck+"---------------");
            if(dateCheck-timeThreshold>10000){
                System.out.println("--------------resolve begin---------------");
                timeThreshold = new Date().getTime();
                packageMgrClient.sendResolveMessage(filters,sorts,count);
                System.out.println("current:"+timeThreshold);
                System.out.println("--------------end---------------");
            }
        }
        return (T)messages.remove();
    }
}
