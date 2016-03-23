package com.yihu.ehr.query;

import javafx.util.Pair;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.hibernate.metadata.ClassMetadata;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * URL 查询串解析器。
 *
 * @author lincl
 * @author Sand
 * @version 1.0
 * @created 2016.02.05 10:17
 */
public class URLHqlQueryParser<T> {
    private String fields;
    private String filters;
    private String orders;

    Session session;
    Class<T> entityCls;

    public URLHqlQueryParser(String fields, String filters, String orders) {
        this.fields = fields;
        this.filters = filters;
        this.orders = orders;
    }

    public URLHqlQueryParser(String filters){
        this.filters = filters;
    }

    public URLHqlQueryParser setSession(Session session) {
        this.session = session;
        return this;
    }

    public URLHqlQueryParser setEntityClass(Class<T> cls) {
        this.entityCls = cls;
        return this;
    }

    /**
     * 生成搜索语句.
     *
     * @return
     */
    public Criteria makeCriteriaQuery() {
        Criteria criteria = session.createCriteria(entityCls);
        ClassMetadata classMetadata = session.getSessionFactory().getClassMetadata(entityCls);

//        makeSelection(criteria, classMetadata);
        makeOrderBy(criteria, classMetadata);
        makeWhere(criteria, classMetadata);

        return criteria;
    }

    /**
     * 生成count语句。
     *
     * @return
     */
    public Criteria makeCriteriaCountQuery() {
        Criteria criteria = session.createCriteria(entityCls);
        ClassMetadata classMetadata = session.getSessionFactory().getClassMetadata(entityCls);
        criteria.setProjection(Projections.rowCount());
        makeWhere(criteria, classMetadata);
        return criteria;
    }

    /**
     * 生成返回值字段。
     *
     * @param criteria
     * @param classMetadata
     */
    private void makeSelection(Criteria criteria, ClassMetadata classMetadata) {

    }

    /**
     * 生成排序字段。
     *
     * @param criteria
     * @param classMetadata
     */
    private void makeOrderBy(Criteria criteria, ClassMetadata classMetadata) {
        if (StringUtils.isNotEmpty(orders)) {
            String[] orderArray = orders.split(",");
            for(String elem : orderArray){
//                try {
//                    classMetadata.getPropertyType(elem);
//                }catch (Exception e){
//                    throw new IllegalArgumentException("the property not found!");
//                }
                criteria = elem.startsWith("+") ?
                        criteria.addOrder(Order.asc(elem.substring(1)))
                        : criteria.addOrder(Order.desc(elem.substring(1)));
            }
        }
    }


    /**
     * like：使用"?"来表示，如：name?'%医'
     * in：使用"="来表示并用","逗号对值进行分隔，如：status=2,3,4,5
     * =：使用"="来表示，如：status=2
     * >=：使用大于号和大于等于语法，如：createDate>2012
     * <=：使用小于号和小于等于语法，如：createDate<=2015
     * 分组：在条件后面加上空格，并设置分组号，如：createDate>2012 g1，具有相同组名的条件将使用or连接
     * 多条件组合：使用";"来分隔
     * <p>
     * 生成 where 条件。
     *
     * @param criteria
     * @param classMetadata
     */
    private void makeWhere(Criteria criteria, ClassMetadata classMetadata) {
        if (StringUtils.isEmpty(filters)) return;

        Map<String, List<Criterion>> criterionMap = new HashMap<>();

        String[] filterArray = filters.split(";");
        List<Criterion> groupCriterion = new ArrayList<>();
        for (int i = 0; i < filterArray.length; ++i) {
            String[] tokens = filterArray[i].split(" ");
            if (tokens.length > 2) throw new IllegalArgumentException("无效过滤参数");

            String group = null;
            if (tokens.length == 2) group = tokens[1];

            Criterion criterion = splitFilter(filterArray[i], classMetadata);

            if (group != null)
                group = Integer.toString(i);

            criterionMap.put(group,
                    makeGroupCriterion(criterionMap.get(group), criterion));
        }
        addWhere(criteria, criterionMap);
    }

    private void addWhere(Criteria criteria, Map<String, List<Criterion>> criterionMap) {
        List<Criterion> ls;
        for (String group : criterionMap.keySet()){
            ls = criterionMap.get(group);
            if(ls.size()>1)
                criteria.add(
                        Restrictions.or(ls.toArray(new Criterion[ls.size()]))
                );
            else
                criteria.add(
                        Restrictions.and(ls.toArray(new Criterion[ls.size()]))
                );
        }
    }

    protected List<Criterion> makeGroupCriterion(List<Criterion> ls, Criterion criterion){
        (ls = ls == null ? new ArrayList<>() : ls)
                .add(criterion);
        return ls;
    }

    protected Criterion splitFilter(String filter, ClassMetadata classMetadata) {
        Criterion criterion = null;
        if (filter.contains("?")) {
            Pair<Property, Object> pair = getPair(filter, "[?]", classMetadata);
            criterion = pair.getKey().like(pair.getValue());
        } else if (filter.contains(">")) {
            Pair<Property, Object> pair = getPair(filter, ">", classMetadata);
            //todo:  转成对应类型
            criterion = pair.getKey().gt(pair.getValue());
        } else if (filter.contains(">=")) {
            Pair<Property, Object> pair = getPair(filter, ">=", classMetadata);
            criterion = pair.getKey().ge(pair.getValue());
        } else if (filter.contains("<")) {
            Pair<Property, Object> pair = getPair(filter, "<", classMetadata);
            criterion = pair.getKey().lt(pair.getValue());
        } else if (filter.contains("<=")) {
            Pair<Property, Object> pair = getPair(filter, "<=", classMetadata);
            criterion = pair.getKey().le(pair.getValue());
        } else if (filter.contains("=")) {
            Pair<Property, Object> pair = getPair(filter, "=", classMetadata);
            if (pair.getValue().getClass().isArray()) {
                criterion = pair.getKey().in((Object[])pair.getValue());
            } else {
                criterion = pair.getKey().eq(pair.getValue());
            }
        }
        return criterion;
    }


    protected Pair<Property, Object> getPair(String filter, String splitter, ClassMetadata classMetadata) throws IllegalArgumentException {
        String[] tokens = filter.split(splitter);
        String valStr = tokens[1];
        Object val = tokens[1];
        try {
            if(splitter.equals("=") && valStr.contains(",")){
                val = formatVal(tokens[0], valStr, true);
            }
            else if(!splitter.equals("[?]")){
                val = formatVal(tokens[0], valStr, false);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return new Pair(Property.forName(tokens[0]), val);
    }

    private Object formatVal(String fileName, String valStr, boolean isArr) throws NoSuchFieldException {
        Object val = "";
        if(isLong(fileName)){
            if(isArr){
                val = strToLongArr(valStr);
            }else
                val = Long.parseLong(valStr);

        }else if(isInteger(fileName)){
            if(isArr){
                val = strToIntArr(valStr);
            }else
                val = Integer.parseInt(valStr);
        }else {
            if(isArr)
                val = valStr.split(",");
            else
                val = valStr;
        }
        return val;
    }

    private Long[] strToLongArr(String valStr){
        String[] strArr = valStr.split(",");
        Long[] longArr = new Long[strArr.length];
        for(int i=0; i<strArr.length; i++){
            longArr[i] = Long.parseLong(strArr[i]);
        }
        return longArr;
    }

    private Integer[] strToIntArr(String valStr){
        String[] strArr = valStr.split(",");
        Integer[] intArr = new Integer[strArr.length];
        for(int i=0; i<strArr.length; i++){
            intArr[i] = Integer.parseInt(strArr[i]);
        }
        return intArr;
    }

    private boolean isInteger(String fieldName) throws NoSuchFieldException {

        Field field = getField(fieldName);
        return field.getType().equals(Integer.class) || field.getType().equals(Integer.TYPE);
    }

    private boolean isLong(String fieldName) throws NoSuchFieldException {

        Field field = getField(fieldName);
        return field.getType().equals(Long.class) || field.getType().equals(Long.TYPE);
    }

    private Field getField(String fieldName) throws NoSuchFieldException {
        Field f;
        try {
            f = entityCls.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            f = entityCls.getSuperclass().getDeclaredField(fieldName);
        }
        return f;
    }
}
