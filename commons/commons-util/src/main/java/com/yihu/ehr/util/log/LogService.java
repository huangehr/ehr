package com.yihu.ehr.util.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogService{

    public static Logger getLogger(final Class<?> clazz){
        return LoggerFactory.getLogger(clazz);
    }

    public static Logger getLogger(final String name){
        return LoggerFactory.getLogger(name);
    }

    public static Logger getLogger(){
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        if(stackTraceElements == null || stackTraceElements.length < 3 ){
            return LoggerFactory.getLogger(LogService.class);
        } else{
            for(int i = 3; i < stackTraceElements.length; i ++) {
                String caller = stackTraceElements[2].getClassName();
                String caller1 = stackTraceElements[i].getClassName();
                if(!caller.equals(caller1)) {
                    return LoggerFactory.getLogger(caller1);
                }
            }
            return LoggerFactory.getLogger(stackTraceElements[2].getClassName());
        }
    }
}
