package com.yihu.ehr.standard.service;

import com.yihu.ehr.query.URLHqlQueryParser;
import com.yihu.ehr.standard.config.StdSessionFactoryBean;
import org.hibernate.*;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author lincl
 * @author Sand
 * @version 1.0
 * @created 2016.2.3
 */
@Transactional(propagation = Propagation.SUPPORTS)
public class BaseHbmService<T> {

    Class<T> entityClass;

    @Autowired
    protected StdSessionFactoryBean localSessionFactoryBean;

    @Autowired
    protected ExtJdbcTemplate jdbcTemplate;

    /************************************************************************/
    /**************  save                        ****************************/
    /************************************************************************/
    public void save(T entity) {
        currentSession().saveOrUpdate(entity);
    }

    /**
     * 插入数据， id策略为increment
     * @param obj
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public T insert(T obj) throws InvocationTargetException, IllegalAccessException, SQLException, NoSuchFieldException, NoSuchMethodException {

        return (T) jdbcTemplate.insert(obj);
    }

    /************************************************************************/
    /**************  find                        ****************************/
    /************************************************************************/
    public T retrieve(Serializable id, Class entityClass) {
        return (T) currentSession().get(entityClass, id);
    }

    public T retrieve(Serializable id) {
        return (T) currentSession().get(getEntityClass(), id);
    }

    public List findAll(){

        return findAll(getEntityClass());
    }

    public List findAll(Class entityClass){
        currentSession().setFlushMode(FlushMode.MANUAL);
        return currentSession().createCriteria(entityClass).list();
    }

    public boolean isExistByField(String field, Object val, Class entityClass){

        return isExistByFields(
                new String[]{field},
                new Object[]{val},
                entityClass
        );
    }

    public boolean isExistByFields(String[] fields, Object[] vals, Class entityClass){
        Criteria criteria = currentSession().createCriteria(entityClass);
        for(int i=0; i< fields.length; i++){
            criteria.add(Restrictions.eq(fields[i], vals[i]));
        }
        criteria.setProjection(Projections.rowCount());
        return ((long) criteria.uniqueResult()) > 0;
    }

    /************************************************************************/
    /**************  delete                      ****************************/
    /************************************************************************/
    public int delete(Serializable id) {
        return delete(id, getEntityClass());
    }

    public int delete(Serializable id, Class entity) {
//        delete(
//                (T) sessionFactory()
//                        .getClassMetadata(entity)
//                        .instantiate(id, ((SessionImpl) currentSession())));
        String hql =
                "DELETE FROM "+ entity.getSimpleName() +
                " WHERE " + sessionFactory().getClassMetadata(entity).getIdentifierPropertyName() + "= ?";
        return currentSession().createQuery(hql)
                .setParameter(0, id)
                .executeUpdate();
    }

    public void delete(T entity) {
        currentSession().delete(entity);
    }

    public int delete(Object[] ids) {
        return delete(ids, getEntityClass());
    }

    public int delete(Object[] ids, Class entity) {
        if(ids==null || ids.length==0)
            return 0;
        String hql =
                "DELETE FROM "+ entity.getSimpleName() +
                        " WHERE " + sessionFactory().getClassMetadata(entity).getIdentifierPropertyName() + " in(:ids) ";
        return currentSession().createQuery(hql)
                .setParameterList("ids", ids)
                .executeUpdate();
    }

    public int deleteByField(String field, Object val, Class entityClass){

        return deleteByFields(new String[]{field}, new Object[]{val}, entityClass);
    }

    public int deleteByField(String field, Object val){

        return deleteByField(field, val, getEntityClass());
    }

    public int deleteByFields(String[] fields, Object[] vals, Class entityClass){
        String hql = "DELETE FROM " + entityClass.getSimpleName();
        String where = "";
        for(int i=0; i<fields.length; i++){
            if(vals[i].getClass().isArray())
                where += "AND " + fields[i] +" in(:v"+i+") ";
            else
                where += "AND " + fields[i] +"=:v"+i+" ";
        }
        if(!where.equals("")){
            hql += " WHERE " + where.substring(4);
        }
        Query query = currentSession().createQuery(hql);
        for (int i=0; i<vals.length; i++){
            if(vals[i].getClass().isArray()){
                query.setParameterList("v" + i, (Object[]) vals[i]);
            }
            else
                query.setParameter("v" + i, vals[i]);
        }
        return query.executeUpdate();
    }

    public int deleteByFields(String[] fields, Object[] vals){
        return deleteByFields(fields, vals, getEntityClass());
    }

    /**************************************************************/
    /************  查询系列方法                  ******************/
    /**************************************************************/
    public List search(String fields, String filters, String sorts, int page, int size) {

        return search(getEntityClass(), fields, filters, sorts, page, size);
    }

    public List search(Class entityClass, String fields, String filters, String sorts, int page, int size) {

        return createHqlQueryParser(entityClass, fields, filters, sorts)
                .makeCriteriaQuery()
                .setFirstResult(getStartRowNum(page, size))
                .setMaxResults(size).list();
    }

    public List search(String fields, String filters, String sorts) {

        return createHqlQueryParser(getEntityClass(), fields, filters, sorts)
                .makeCriteriaQuery()
                .list();
    }

    public List search(Class entityClass, String fields, String filters, String sorts) {

        return createHqlQueryParser(entityClass, fields, filters, sorts)
                .makeCriteriaQuery()
                .list();
    }

    public List search(String fields, String filters) {

        return search(getEntityClass(), fields, filters, "");
    }

    public List search(Class entityClass, String fields, String filters) {

        return search(entityClass, fields, filters, "");
    }

    public List search(String filters) {

        return search(entityClass, "", filters, "");
    }

    public List search(Class entityClass, String filters) {

        return search(entityClass, "", filters, "");
    }

    public long getCount(String filters) {

        return (long) createHqlQueryParser(getEntityClass(), filters)
                .makeCriteriaCountQuery()
                .uniqueResult();
    }

    public long getCount(Class entityClass, String filters) {

        return (long) createHqlQueryParser(entityClass, filters)
                .makeCriteriaCountQuery()
                .uniqueResult();
    }

    protected <T> URLHqlQueryParser createHqlQueryParser(String fields, String filters, String orders) {

        return createHqlQueryParser(getEntityClass(), fields, filters, orders);
    }

    protected <T> URLHqlQueryParser createHqlQueryParser(Class entity, String fields, String filters, String orders) {

        currentSession().setFlushMode(FlushMode.MANUAL);
        URLHqlQueryParser queryParser = new URLHqlQueryParser<T>(fields, filters, orders)
                .setSession(currentSession())
                .setEntityClass(entity);

        return queryParser;
    }

    protected <T> URLHqlQueryParser createHqlQueryParser(String filters) {

        return createHqlQueryParser(getEntityClass(), filters);
    }

    protected <T> URLHqlQueryParser createHqlQueryParser(Class entity, String filters) {

        return createHqlQueryParser(entity, "", filters, "");
    }


    /**************************************************************/
    /************  others                        ******************/
    /**************************************************************/
    public Class<T> getEntityClass() {
        return entityClass;
    }

    public int getStartRowNum(int page, int size){
        page = page == 0 ? 1 : page;
        return (page - 1) * size;
    }

    protected SessionFactory sessionFactory(){
        return localSessionFactoryBean.getObject();
    }

    protected Session currentSession() {
        return localSessionFactoryBean.getObject().getCurrentSession();
    }

}
