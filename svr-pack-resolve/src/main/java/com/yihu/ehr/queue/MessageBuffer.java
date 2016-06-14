package com.yihu.ehr.queue;

import org.springframework.stereotype.Component;

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

    public synchronized void putMessage(Object message){
        messages.add(message);
    }

    public synchronized <T> T getMessage(){
        return (T)messages.remove();
    }
}
