package com.yihu.ehr.query;

import com.yihu.ehr.XCustomRepository;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.parm.PageModel;
import org.hibernate.Query;
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
import java.util.List;
import java.util.Map;

/**
 * Service基础类。此类基于Spring Data JPA进行封装（Spring Data JPA又是基于JPA封装，EHR平台使用Hibernate作为JPA实现者）。
 * 需要注意的是，部分功能会跳过JPA接口而直接使用Hibernate接口，比如访问Hibernate的Session接口，因为它把JPA的EntityManager功能强大。
 *
 * @author lincl
 * @author Sand
 * @version 1.0
 * @created 2016.2.3
 */
public class BaseService<T, R> {
    @Autowired
    R repo;

    @PersistenceContext
    protected EntityManager entityManager;

    public T findOne(Serializable id) {
        return (T) getRepo().findOne(id);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Serializable id) {
        getRepo().delete(id);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(T entity) {
        getRepo().delete(entity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public int delete(Iterable ids) {
        getRepo().deleteCollection(ids);

        return 0;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void save(T entity) {
        getRepo().save(entity);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public Envelop pagesToEnvelop(PageModel pageModel) {
        pageModel.setModelClass(getModelClass());
        List ls = pages(pageModel);
        Integer totalCount = totalCountForPage(pageModel);
        return getEnvelop(ls, totalCount, pageModel.getPage(), pageModel.getRows());
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List pages(PageModel pageModel) {
        pageModel.setModelClass(getModelClass());
        Session session = currentSession();
        String hql = "select tb from " + getModelName() + " tb ";
        String wh = pageModel.format();
        hql += wh.equals("") ? "" : wh;
        Query query = setQueryVal(session.createQuery(hql), pageModel);
        int page = pageModel.getPage();
        if (page > 0) {
            query.setMaxResults(pageModel.getRows());
            query.setFirstResult((page - 1) * pageModel.getRows());
        }
        return query.list();
    }

    public int totalCountForPage(PageModel pageModel) {
        pageModel.setModelClass(getModelClass());
        Session session = currentSession();
        String hql = "select count(*) from " + getModelName() + " ";
        String wh = pageModel.format();
        hql += wh.equals("") ? "" : wh;
        Query query = setQueryVal(session.createQuery(hql), pageModel);
        return ((Long) query.list().get(0)).intValue();
    }

    protected Query setQueryVal(Query query, PageModel pageModel) {
        Map<String, FieldCondition> filters = pageModel.getFilters();
        for (String k : filters.keySet()) {
            if (filters.get(k).getLogic().equals("in"))
                query.setParameterList(k, (Object[]) filters.get(k).formatVal());
            else if(filters.get(k).getLogic().equals("between")){
                List ls = filters.get(k).getVal();
                query.setParameter(k + "1", ls.get(0));
                query.setParameter(k + "2", ls.get(1));
            }
            else
                query.setParameter(k, filters.get(k).formatVal());
        }
        return query;
    }

    public String getModelName() {
        return getModelClass().getSimpleName();
    }

    public Class getModelClass() {
        Type genType = this.getClass().getGenericSuperclass();
        Type[] parameters = ((ParameterizedType) genType).getActualTypeArguments();
        return (Class) parameters[0];
    }

    /**
     * 获取当前session
     *
     * @return
     */
    protected Session currentSession() {
        return entityManager.unwrap(Session.class);
    }

    protected XCustomRepository getRepo() {
        return (XCustomRepository) repo;
    }

    protected R getRepository() {
        return repo;
    }

    protected Envelop getEnvelop(List detaiModelList, int totalCount, int currPage, int rows) {
        Envelop Envelop = new Envelop();
        Envelop.setSuccessFlg(true);
        Envelop.setDetailModelList(detaiModelList);
        Envelop.setTotalCount(totalCount);

        return Envelop;
    }

    public <T> URLQueryParser createQueryParser(String fields, String filters, String orders, Class<T> entityClass){
        URLQueryParser queryParser = new URLQueryParser<T>(fields, filters, orders)
                .setEntityManager(entityManager)
                .setEntityClass(entityClass);

        return queryParser;
    }

    public List<T> search(CriteriaQuery query, int page, int size){
        return entityManager
                .createQuery(query)
                .setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultList();
    }

    public long getCount(CriteriaQuery countQuery){
        String sqlString = entityManager.createQuery(countQuery).unwrap(org.hibernate.Query.class).getQueryString();

        return (long)entityManager.createQuery(countQuery).getSingleResult();
    }
}
