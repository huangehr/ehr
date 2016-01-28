package com.yihu.ehr.adaption.commons;

import org.hibernate.Session;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by Lincl on 2016/1/26.
 */
public class BaseManager {
    @PersistenceContext
    EntityManager entityManager;

    /**
     * 获取当前session
     * @return
     */
    protected Session currentSession(){
        return entityManager.unwrap(Session.class);
    }
}
