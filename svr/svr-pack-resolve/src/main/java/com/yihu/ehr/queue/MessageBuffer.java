package com.yihu.ehr.queue;

import com.yihu.ehr.feign.PackageMgrClient;
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

    private Queue<Object> messages = new LinkedList<>();
    private long timeThreshold = new Date().getTime();

    @Autowired
    private PackageMgrClient packageMgrClient;

    public synchronized void putMessage(Object message){
        messages.add(message);
    }

    public synchronized <T> T getMessage() {
        //System.out.println("--------------begin------------------");
        String filters = "archiveStatus=Received";
        String sorts = "-receiveDate";
        int count = 500;
        if(messages.isEmpty()){
            long dateCheck = new Date().getTime();
            //System.out.println("--------------timeThreshold："+timeThreshold+"---------------");
            //System.out.println("--------------current ："+dateCheck+"---------------");
            if(dateCheck - timeThreshold > 10000){
                //System.out.println("--------------resolve begin---------------");
                timeThreshold = new Date().getTime();
                packageMgrClient.sendResolveMessage(filters,sorts,count);
                //System.out.println("current:"+timeThreshold);
                //System.out.println("--------------end---------------");
            }
        }
        if(messages.isEmpty()) {
            return null;
        }else {
            return (T)messages.remove();
        }
    }
}
