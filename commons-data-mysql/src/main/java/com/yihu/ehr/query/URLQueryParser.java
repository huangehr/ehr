package com.yihu.ehr.query;

import com.yihu.ehr.util.DateTimeUtils;
import javafx.util.Pair;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.NumberUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.text.ParseException;
import java.util.*;

/**
 * URL 查询串解析器。将 {@link com.yihu.ehr.util.URLQueryBuilder} 中产生的查询字符串反解析。
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

    public URLQueryParser(String filters) {
        this.filters = filters;
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
    public CriteriaQuery makeCriteriaQuery() throws ParseException {
        CriteriaQuery query = builder.createQuery();
        Root<T> root = query.from(entityCls);

        makeSelection(builder, query, root);
        makeOrderBy(builder, query, root);
        makeWhere(builder, query, root);

        return query;
    }

    /**
     * 生成count语句。
     *
     * @return
     */
    public CriteriaQuery makeCriteriaCountQuery() throws ParseException {
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<T> root = query.from(entityCls);

        query.select(builder.count(root));

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
        if (false/*StringUtils.isNotEmpty(fields)*/) {
            String[] fieldArray = fields.split(",");

            List<Selection<T>> selections = new ArrayList<>(fieldArray.length);
            Arrays.stream(fieldArray).forEach(elem -> selections.add(root.get(elem)));

            query.select(criteriaBuilder.tuple(selections.toArray(new Selection[selections.size()])));
        } else {
            query.select(root);
        }
    }

    /**
     * +code 以code字段进行升序排序
     * -code 以code字段进行降序排序
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
     * like：使用"?"来表示，如：name?'%医'
     * in：使用"="来表示并用","逗号对值进行分隔，如：status=2,3,4,5
     * not in：使用"<>"来表示并用","逗号对值进行分隔，如：status=2,3,4,5
     * =：使用"="来表示，如：status=2
     * >=：使用大于号和大于等于语法，如：createDate>2012
     * <=：使用小于号和小于等于语法，如：createDate<=2015
     * 分组：在条件后面加上空格，并设置分组号，如：createDate>2012 g1，具有相同组名的条件将使用or连接
     * 多条件组合：使用";"来分隔
     * <p>
     * 生成 where 条件。
     *
     * @param criteriaBuilder
     * @param query
     * @param root
     */
    private void makeWhere(CriteriaBuilder criteriaBuilder, CriteriaQuery query, Root<T> root) throws ParseException {
        if (StringUtils.isEmpty(filters)) return;

        Map<String, Predicate> predicateMap = new HashMap<>();

        String[] filterArray = filters.split(";");

        for (int i = 0; i < filterArray.length; ++i) {

            String[] tokens = filterArray[i].split(" ");
            if (tokens.length > 2) throw new IllegalArgumentException("无效过滤参数");

            String group = null;
            if (tokens.length == 2) group = tokens[1];

            Predicate predicate = splitFilter(tokens[0], criteriaBuilder, root);

            if (group != null) {
                if (predicateMap.get(group) == null)
                    predicateMap.put(group, predicate);
                else
                    predicateMap.put(group, criteriaBuilder.or(predicateMap.get(group), predicate));
            } else
                predicateMap.put(Integer.toString(i), predicate);
        }

        query.where(predicateMap.values().toArray(new Predicate[predicateMap.size()]));
    }

    protected Predicate splitFilter(String filter, CriteriaBuilder cb, Root<T> root) throws ParseException {
        Predicate predicate = null;
        if (filter.contains("?")) {
            Pair<Path, String> pair = getPair(filter, "[?]", root);
            predicate = cb.like(pair.getKey(), "%" + pair.getValue() + "%");
        } else if (filter.contains("<>")) {
            Pair<Path, String> pair = getPair(filter, "<>", root);

            if (pair.getValue().contains(",")) {
                predicate = cb.not(pair.getKey().in(pair.getValue().split(",")));
            } else {
                predicate = cb.notEqual(pair.getKey(), pair.getValue());
            }
        } else if (filter.contains(">=")) {
            Pair<Path, String> pair = getPair(filter, ">=", root);
            predicate = cb.ge(pair.getKey(), NumberUtils.parseNumber(pair.getValue(), pair.getKey().getJavaType()));
        } else if (filter.contains(">")) {
            Pair<Path, String> pair = getPair(filter, ">", root);
            if (pair.getKey().getJavaType() == Date.class) {
                Date date = DateTimeUtils.simpleDateParse(pair.getValue());
                predicate = cb.greaterThan(pair.getKey(), date);
            } else {
                predicate = cb.gt(pair.getKey(), NumberUtils.parseNumber(pair.getValue(), pair.getKey().getJavaType()));
            }
        } else if (filter.contains("<=")) {
            Pair<Path, String> pair = getPair(filter, "<=", root);
            predicate = cb.le(pair.getKey(), NumberUtils.parseNumber(pair.getValue(), pair.getKey().getJavaType()));
        } else if (filter.contains("<")) {
            Pair<Path, String> pair = getPair(filter, "<", root);
            predicate = cb.lt(pair.getKey(), NumberUtils.parseNumber(pair.getValue(), pair.getKey().getJavaType()));
        } else if (filter.contains("=")) {
            Pair<Path, String> pair = getPair(filter, "=", root);

            Set<Object> values = new HashSet<>();
            for (String data : pair.getValue().split(",")){
                if (pair.getKey().getJavaType().isEnum()){
                    values.add(Enum.valueOf(pair.getKey().getJavaType(), data));
                } else if(pair.getKey().getJavaType().equals(Boolean.TYPE)){
                    values.add(BooleanUtils.toBoolean(data));
                } else {
                    values.add(data);
                }
            }

            predicate = pair.getKey().in(values);
        }

        return predicate;
    }

    protected Pair<Path, String> getPair(String filter, String splitter, Root<T> root) {
        String[] tokens = filter.split(splitter);
        return new Pair<>(root.get(tokens[0]), tokens[1]);
    }
}
