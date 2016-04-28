package com.yihu.ehr.pack.task;

import com.yihu.ehr.model.packs.MPackage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.04.01 14:38
 */
@Service
public class MessageBuffer {
    Set<MPackage> messages = new HashSet<>();

    @Async
    public synchronized void putMessage(MPackage message){
        this.messages.add(message);
    }

    public synchronized Set<MPackage> pickMessages(){
        Set<MPackage> temp = new HashSet<>(messages);

        messages.clear();

        return temp;
    }
}
