package com.yihu.ehr.std.data;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.List;

/**
 * 基础DAO。目的是为了简化在各个业务类访问数据库的时候, 不需要再通过注入 sessionFactory, 即以下代码:
 *
 * Session session = sessionFactory.getCurrentSession();
 * session.doSomething(object);
 *
 * 此DAO功能目前仅在 svr-standard 服务中使用，因为其他地方使用JPA Repository来访问数据库。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.07.09 17:08
 */
@Transactional
public class SQLGeneralDAO extends HibernateDaoSupport implements XSQLGeneralDAO {
    /**
     * 注入SessionFactory, 各派生DAO不用再手工配置.
     *
     * @param sessionFactory
     */
    @Resource( name = "sessionFactory")
    void injectSessionFactory(SessionFactory sessionFactory){
        super.setSessionFactory(sessionFactory);
    }

    public SQLGeneralDAO(){}

    @Override
    public void saveEntity(Object entity){
        if(entity == null) return;

        getHibernateTemplate().save(entity);
    }

    @Override
    public void updateEntity(Object entity){
        if(entity == null) return;

        getHibernateTemplate().update(entity);
    }

    @Override
    public void mergeEntity(Object entity){
        if(entity == null) return;

        getHibernateTemplate().merge(entity);
    }

    @Override
    public void deleteEntity(Object entity){
        if(entity == null) return;

        getHibernateTemplate().delete(entity);
    }

    @Override
    public <T> T getEntity(Class<T> cls, Serializable id){
        return (T)getHibernateTemplate().get(cls, id);
    }

    @Override
    public <T> T[] getEntityList(Class<T> cls, String hql){
        Query query = currentSession().createQuery(hql);
        List<Object> entities = query.list();
        return entities.toArray((T[])Array.newInstance(cls, entities.size()));
    }

    @Override
    public Session openSession() {
        return getHibernateTemplate().getSessionFactory().openSession();
    }

    @Override
    public Session getCurrentSession(){
        return currentSession();
    }
}
