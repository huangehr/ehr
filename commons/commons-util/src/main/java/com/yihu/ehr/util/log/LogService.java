package com.yihu.ehr.util.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;

/**
 * 日志服务类。目前使用的是 log4j2，即2.X版本的log4j,不能与其他版本混淆,因为它们的配置文件加载方式不一致.
 *
 * @apiNote 由于涉及到Spring框架的初始化, 它会先于log4j的初始化. 如果在log4j配置未初始化之前在Spring Bean内先生成
 * log4j对象, 可能会造成错误. 例如: Spring MVC 的Controller部分可能会缓存一个Logger对象,但此时log4j是未配置的, 所以会出现异常.
 * 正确的作法是Spring的Bean内不要缓存这个Logger对象,并在lo4j初始化后在运行时去获取Logger对象.
 *
 * @author Air
 * @author Sand
 * @version 1.0
 * @created 2015.06.18 16:59
 */
public class LogService{
    static{
        try {
            String configPath = LogService.class.getResource("/").getPath() + "log4j/log4j2.xml";

            ConfigurationSource source;
            source = new ConfigurationSource(new FileInputStream(configPath));

            Configurator.initialize(null, source);
        } catch (Exception ex) {
            // 此处不能再调用自身作为日志记录服务, 因为此时初始化失败了
            LogManager.getLogger(LogService.class).error(ex.getMessage());
        }
    }


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
