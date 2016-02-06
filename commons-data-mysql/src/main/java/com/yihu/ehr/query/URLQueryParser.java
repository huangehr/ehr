package com.yihu.ehr.query;

import org.apache.commons.lang.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * URL 查询串解析器。将 {@link URLQueryBuilder} 中产生的查询字符串反解析。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.02.05 10:17
 */
public class URLQueryParser<T> {
    private String fields;
    private String filters;
    private String orders;

    EntityManager entityManager;
    CriteriaBuilder builder;


    Class<T> entityCls;

    public URLQueryParser(String fields, String filters, String orders) {
        this.fields = fields;
        this.filters = filters;
        this.orders = orders;
    }

    public URLQueryParser setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
        builder = entityManager.getCriteriaBuilder();

        return this;
    }

    public URLQueryParser setEntityClass(Class<T> cls) {
        this.entityCls = cls;

        return this;
    }

    /**
     * 生成搜索语句.
     *
     * @return
     */
    public CriteriaQuery makeCriteriaQuery() {
        CriteriaQuery query = builder.createQuery();
        Root<T> root = query.from(entityCls);

        makeSelection(builder, query, root);
        makeOrderBy(builder, query, root);
        makeWhere(builder, query, root);

        return query;
    }

    public CriteriaQuery makeCriteriaCountQuery() {
        CriteriaQuery query = builder.createQuery();
        Root<T> root = query.from(entityCls);

        query.select(builder.count(query.from(entityCls)));

        makeWhere(builder, query, root);

        return query;
    }

    /**
     * 生成返回值字段。
     *
     * @param criteriaBuilder
     * @param query
     * @param root
     */
    private void makeSelection(CriteriaBuilder criteriaBuilder, CriteriaQuery query, Root<T> root) {
        if (StringUtils.isNotEmpty(fields)) {
            String[] fieldArray = fields.split(",");

            List<Selection<T>> selections = new ArrayList<>(fieldArray.length);
            Arrays.stream(fieldArray).forEach(elem -> selections.add(root.get(elem)));

            query.select(criteriaBuilder.tuple(selections.toArray(new Selection[selections.size()])));
        } else {
            query.select(root);
        }
    }

    /**
     * 生成排序字段。
     *
     * @param criteriaBuilder
     * @param query
     * @param root
     */
    private void makeOrderBy(CriteriaBuilder criteriaBuilder, CriteriaQuery query, Root<T> root) {
        if (StringUtils.isNotEmpty(orders)) {
            String[] orderArray = orders.split(",");

            List<Order> orderList = new ArrayList<>(orderArray.length);
            Arrays.stream(orderArray).forEach(
                    elem -> orderList.add(
                            elem.startsWith("+") ?
                                    criteriaBuilder.asc(root.get(elem.substring(1))) : criteriaBuilder.desc(root.get(elem.substring(1)))));

            query.orderBy(orderList);
        }
    }

    /**
     * 生成 where 条件。
     *
     * @param criteriaBuilder
     * @param query
     * @param root
     */
    private void makeWhere(CriteriaBuilder criteriaBuilder, CriteriaQuery query, Root<T> root) {

    }
}
