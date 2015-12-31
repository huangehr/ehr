package com.yihu.ehr.data;

import org.hibernate.Session;

import java.io.Serializable;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.08.06 12:44
 */
public interface XSQLGeneralDAO {
    public void saveEntity(Object entity);

    public void updateEntity(Object entity);

    public void mergeEntity(Object entity);

    public void deleteEntity(Object entity);

    public <T> T getEntity(Class<T> cls, Serializable id);

    public <T> T[] getEntityList(Class<T> cls, String hql);

    public Session getCurrentSession();

    public Session openSession();
}
