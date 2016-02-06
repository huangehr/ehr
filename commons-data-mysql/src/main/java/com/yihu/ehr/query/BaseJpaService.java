package com.yihu.ehr.query;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;

/**
 * Service基础类。此类基于Spring Data JPA进行封装（Spring Data JPA又是基于JPA封装，EHR平台使用Hibernate作为JPA实现者）。
 * 需要注意的是，部分功能会跳过JPA接口而直接使用Hibernate接口，比如访问Hibernate的Session接口，因为它把JPA的EntityManager功能强大。
 *
 * @author lincl
 * @author Sand
 * @version 1.0
 * @created 2016.2.3
 */
@Transactional(propagation = Propagation.SUPPORTS)
public class BaseJpaService<T, R> {
    @Autowired
    R repo;

    @PersistenceContext
    protected EntityManager entityManager;

    public void save(T entity) {
        getRepository().save(entity);
    }

    public T retrieve(Serializable id) {
        return (T) getRepository().findOne(id);
    }

    public void delete(Serializable id) {
        getRepository().delete(id);
    }

    public void delete(T entity) {
        getRepository().delete(entity);
    }

    public void delete(Iterable ids) {
        Iterator iterator = ids.iterator();
        while (iterator.hasNext()) {
            Serializable id = (Serializable) iterator.next();
            getRepository().delete(id);
        }
    }

    public Class<T> getEntityClass() {
        Type genType = this.getClass().getGenericSuperclass();
        Type[] parameters = ((ParameterizedType) genType).getActualTypeArguments();
        return (Class) parameters[0];
    }

    public List search(String fields, String filters, String sorts, int page, int size) {
        URLQueryParser queryParser = createQueryParser(fields, filters, sorts);
        CriteriaQuery query = queryParser.makeCriteriaQuery();

        return entityManager
                .createQuery(query)
                .setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultList();
    }

    public long getCount(String filters) {
        URLQueryParser queryParser = createQueryParser(filters);
        CriteriaQuery query = queryParser.makeCriteriaCountQuery();

        if (true) {
            System.out.println(getSQLQueryString(query));
        }

        return (long) entityManager.createQuery(query).getSingleResult();
    }

    /**
     * 因为底层使用的是Hibernate，所以对Query解封之后可以得到Hibernate的Query对象，并取得原始SQL查询语句。
     *
     * @param query
     * @return
     */
    protected String getSQLQueryString(CriteriaQuery query) {
        String sqlString = entityManager.createQuery(query).unwrap(org.hibernate.Query.class).getQueryString();

        return sqlString;
    }

    protected <T> URLQueryParser createQueryParser(String fields, String filters, String orders) {
        URLQueryParser queryParser = new URLQueryParser<T>(fields, filters, orders)
                .setEntityManager(entityManager)
                .setEntityClass(getEntityClass());

        return queryParser;
    }

    protected <T> URLQueryParser createQueryParser(String filters) {
        URLQueryParser queryParser = new URLQueryParser<T>(filters)
                .setEntityManager(entityManager)
                .setEntityClass(getEntityClass());

        return queryParser;
    }

    protected Session currentSession() {
        return entityManager.unwrap(Session.class);
    }

    protected PagingAndSortingRepository getRepository() {
        return (PagingAndSortingRepository) repo;
    }
}
