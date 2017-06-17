package com.yihu.quota.config.quota;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by chenweida on 2016/2/3.
 */
@Configuration
public class SchedulerConfig {
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private JobFactory jobFactory;
    @Autowired
    private DataSource dataSource;
    @Bean
    SchedulerFactoryBean schedulerFactoryBean() throws IOException {
        SchedulerFactoryBean bean = new SchedulerFactoryBean();
        bean.setJobFactory(jobFactory);
        bean.setApplicationContext(this.applicationContext);
        bean.setOverwriteExistingJobs(true);
        bean.setStartupDelay(20);// 延时启动
        bean.setSchedulerName("schedulerFactoryBeanCWD");
        bean.setAutoStartup(true);
        bean.setDataSource(dataSource);
        bean.setQuartzProperties(quartzProperties());
        return bean;
    }

    /**
     * quartz配置文件
     * @return
     * @throws IOException
     */
    @Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }
}

