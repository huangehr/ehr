package com.yihu.ehr.lang;

import com.yihu.ehr.constrant.Services;
import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.TProcessor;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * 服务注册。需要在所有的Bean都初始化之后再注册这些Processor. 使用 Thrift 的多处理器注册服务。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.12.08 15:45
 */
public class ServiceRegister {
    private static ApplicationContext springContext = null;

    /**
     * 获取Spring应用上下文环境。
     *
     * @return
     */
    public static ApplicationContext getApplicationContext() {
        return springContext;
    }


}
