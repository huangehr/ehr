package com.yihu.ehr.config;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;

/**
 * @author lincl
 * @version 1.0
 * @created 2016/2/18
 */
public class StdSessionFactoryBean extends LocalSessionFactoryBean {

    private SessionFactory sessionFactory;

    public synchronized SessionFactory addClassToBuildSessionFactory(Class ... annotatedClasses){
        Configuration cfg = getConfiguration();
        for (Class c : annotatedClasses){
            if(cfg.getClassMapping(c.getName())==null)
                cfg.addAnnotatedClass(c);
        }
        return sessionFactory = super.buildSessionFactory((LocalSessionFactoryBuilder) cfg);
    }

    @Override
    public SessionFactory getObject() {
        if(sessionFactory==null)
            sessionFactory = super.getObject();
        return sessionFactory;
    }
}
