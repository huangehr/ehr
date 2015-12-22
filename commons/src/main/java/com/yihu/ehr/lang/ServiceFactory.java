package com.yihu.ehr.lang;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 服务管理器。服务管理器是所有顶级服务的生成器。若是单元测试使用此服务工厂，Spring Context会被ServiceFactory创建。
 * 若是在Tomcat中使用此服务工厂，Spring Context会由 Tomcat 创建，并通过 ApplicationContextAware 赋值给 ServiceFactory。
 *
 * @author Sand
 * @version 1.0
 * @created 12-05-2015 17:47:55
 */
@Component
public class ServiceFactory implements ApplicationContextAware {
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
        if (springContext == null) {
            springContext = new ClassPathXmlApplicationContext(new String[]
                    {
                            "spring/applicationContext.xml",
                            "spring/applicationContextEx.xml"
                    }
            );
        }

        return (T) springContext.getBean(serviceName);
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
        T ref = getService(serviceName);
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

    // static block
    static {
        String configPath = ServiceFactory.class.getResource("/").getPath() + "log4j/log4j2.xml";
        System.setProperty("log4j.configurationFile", configPath);  // 由于Spring启动时就依赖到Log4j，在此处就直接设置系统属性
    }
}
