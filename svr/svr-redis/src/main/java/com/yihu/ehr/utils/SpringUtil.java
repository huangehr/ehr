package com.yihu.ehr.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

/**
 * @author 张进军
 * @date 2017/11/9 13:37
 */
@Component
public class SpringUtil implements ApplicationContextAware {

    private static ApplicationContext appContext = null;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        appContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return appContext;
    }

    public static void autowiredBean(Object bean) {
        autowiredBean(bean, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE);
    }

    public static void autowiredBean(Object bean, int autowireMode) {
        String beanName = ClassUtils.getUserClass(bean).getName();
        AutowireCapableBeanFactory factory = appContext.getAutowireCapableBeanFactory();
        factory.autowireBeanProperties(bean, autowireMode, false);
        factory.initializeBean(bean, beanName);
    }

}
