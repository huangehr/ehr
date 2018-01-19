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
        if(stackTraceElements == null && stackTraceElements.length > 3){
            return LoggerFactory.getLogger(LogService.class);
        } else{
            return LoggerFactory.getLogger(stackTraceElements[2].getClassName());
        }
    }
}
