package com.yihu.ehr.query;

import com.yihu.ehr.constants.PageArg;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
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

    public T save(T entity) {
        return (T) getRepository().save(entity);
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
        List<T> list = getJpaRepository().findAll(ids);
        getJpaRepository().deleteInBatch(list);
    }

    public Class<T> getEntityClass() {
        Type genType = this.getClass().getGenericSuperclass();
        Type[] parameters = ((ParameterizedType) genType).getActualTypeArguments();
        return (Class) parameters[0];
    }

    public List search(String fields, String filters, String sorts, Integer page, Integer size) {
        URLQueryParser queryParser = createQueryParser(fields, filters, sorts);
        CriteriaQuery query = queryParser.makeCriteriaQuery();

        if (page == null || page <= 0) page = PageArg.DefaultPage;
        if (size == null || size <= 0 || size >= 1000) size = PageArg.DefaultSize;

        return entityManager
                .createQuery(query)
                .setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultList();
    }

    public List search(String filters) {
        URLQueryParser queryParser = createQueryParser("", filters, "");
        CriteriaQuery query = queryParser.makeCriteriaQuery();

        return entityManager
                .createQuery(query)
                .getResultList();
    }

    public long getCount(String filters) {
        URLQueryParser queryParser = createQueryParser(filters);
        CriteriaQuery query = queryParser.makeCriteriaCountQuery();

        return (long) entityManager.createQuery(query).getSingleResult();
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

    protected Sort parseSorts(String sorter){
        if (StringUtils.isNotEmpty(sorter)) {
            String[] orderArray = sorter.split(",");

            List<Sort.Order> orderList = new ArrayList<>(orderArray.length);
            Arrays.stream(orderArray).forEach(
                    elem -> orderList.add(
                            elem.startsWith("+") ? new Sort.Order(Sort.Direction.ASC, elem.substring(1)):
                                    new Sort.Order(Sort.Direction.DESC, elem.substring(1))));

            return new Sort(orderList);
        }

        return null;
    }

    protected Session currentSession() {
        return entityManager.unwrap(Session.class);
    }

    public PagingAndSortingRepository getRepository() {
        return (PagingAndSortingRepository) repo;
    }

    public JpaRepository getJpaRepository(){
        return (JpaRepository)repo;
    }

    public List<T> findByField(String field, Object value){

        return findByFields(
                new String[]{field},
                new Object[]{value}
        );
    }

    public List<T> findByFields(String[] fields, Object[] values){
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery query = criteriaBuilder.createQuery(getEntityClass());
        Root<T> root = query.from(getEntityClass());
        List<Predicate> ls = new ArrayList<>();
        for(int i=0; i< fields.length; i++){
            if(values[i].getClass().isArray())
                ls.add(criteriaBuilder.in(root.get(fields[i]).in((Object[])values[i])));
            else
                ls.add(criteriaBuilder.equal(root.get(fields[i]), values[i]));
        }
        query.where(ls.toArray(new Predicate[ls.size()]));
        return entityManager
                .createQuery(query)
                .getResultList() ;
    }
}
