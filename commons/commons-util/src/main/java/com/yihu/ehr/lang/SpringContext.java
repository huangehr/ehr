package com.yihu.ehr.lang;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

/**
 * Spring上下文管理器。
 *
 * @author Sand
 * @version 1.0
 * @created 12-05-2015 17:47:55
 */
@Component
public class SpringContext implements ApplicationContextAware {
    private static ApplicationContext springContext = null;

    /**
     * 获取Spring应用上下文环境。
     *
     * @return
     */
    public static ApplicationContext getApplicationContext() {
        return springContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        springContext = applicationContext;
    }

    /**
     * 获取服务。
     *
     * @param serviceName
     * @param <T>
     * @return
     */
    public static <T> T getService(String serviceName) {
        return (T) springContext.getBean(serviceName);
    }

    public static <T> T getService(Class<T> beanCls) {
        return (T) springContext.getBean(beanCls);
    }

    /**
     * 获取服务，并用参数初始化对象。
     *
     * @param serviceName
     * @param args
     * @param <T>
     * @return
     */
    public static <T> T getService(String serviceName, Object... args) {
        T ref = (T)springContext.getBean(serviceName, args);
        if (ref == null) return null;

        return ref;
    }

    public static <T> T getService(Class<T> beanCls, Object... args){
        T ref = (T)springContext.getBean(beanCls, args);
        if (ref == null) return null;

        return ref;
    }

    /**
     * 获取平台支持的所有服务名称。
     *
     * @return
     */
    public static String[] getAvailableServiceNames() {
        String[] serviceNames = springContext.getBeanDefinitionNames();

        return serviceNames;
    }

    /**
     * 判断是否支持特定服务。
     *
     * @param serviceName
     * @return
     */
    public static boolean isServiceSupported(String serviceName) {
        return springContext.containsBeanDefinition(serviceName);
    }

    /**
     * 获取服务的实现类。
     *
     * @param serviceName
     * @return
     */
    public static Class getServiceType(String serviceName) {
        return springContext.getType(serviceName);
    }

    /**
     * 判断服务是否为单例模式。
     *
     * @param serviceName
     * @return
     */
    public static boolean isSingleton(String serviceName) {
        return springContext.isSingleton(serviceName);
    }

    /**
     * 手动装配Bean
     * @param bean
     */
    public static void autowiredBean(Object bean) {
        autowiredBean(bean, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE);
    }

    /**
     * 指定模式，手动装配Bean
     * @param bean
     * @param autowireMode
     */
    public static void autowiredBean(Object bean, int autowireMode) {
        String beanName = ClassUtils.getUserClass(bean).getName();
        AutowireCapableBeanFactory factory = springContext.getAutowireCapableBeanFactory();
        factory.autowireBeanProperties(bean, autowireMode, false);
        factory.initializeBean(bean, beanName);
    }
}
