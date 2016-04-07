package com.yihu.ehr.adapter.service;

import org.springframework.util.StringUtils;

import java.util.HashMap;

/**
 * @author lincl
 * @version 1.0
 * @created 2016/3/22
 */
public class PageParms extends HashMap<String, Object> {

    public static String LIKE = "?";
    public static String EQUAL = "=";
    public static String NOT_EQUAL = "<>";
    public static String GREATER_THAN = ">";
    public static String GREATER_EQUAL = ">=";
    public static String LESS_THAN = "<";
    public static String LESS_EQUAL = "<=";

    private int rows = 999;
    private int page = 1;
    private String fields = "";
    private String filters = "";
    private String sorts = "";

    public PageParms() {
        setValues();
    }

    public PageParms(String fields, String filters, String sorts, int size, int page) {

        this.fields = fields;
        this.filters = filters;
        this.sorts = sorts;
        this.rows = size;
        this.page = page;

        setValues();
    }

    public PageParms(String sorts, int size, int page) {

        this.sorts = sorts;
        this.page = page;
        this.rows = size;
        setValues();
    }

    public PageParms(int size, int page) {

        this.page = page;
        this.rows = size;
        setValues();
    }

    public PageParms(String filters, int size) {

        this.filters = filters;
        this.rows = size;
        setValues();
    }

    public PageParms(String filters) {

        this.filters = filters;
        setValues();
    }

    /**
     * 模糊
     *
     * @param field
     * @param value
     * @return
     */
    public PageParms addLike(String field, Object value) {

        return addLogic(field, LIKE, value);
    }

    public PageParms addLikeNotNull(String field, Object value) {

        return addLogicNotNull(field, LIKE, value);
    }

    /**
     * 小于
     *
     * @param field
     * @param value
     * @return
     */
    public PageParms addLessThan(String field, Object value) {

        return addLogic(field, LESS_THAN, value);
    }

    public PageParms addLessThanNotNull(String field, Object value) {

        return addLogicNotNull(field, LESS_THAN, value);
    }

    /**
     * 小于等于
     *
     * @param field
     * @param value
     * @return
     */
    public PageParms addLessEqual(String field, Object value) {

        return addLogic(field, LESS_EQUAL, value);
    }

    public PageParms addLessEqualNotNull(String field, Object value) {

        return addLogicNotNull(field, LESS_EQUAL, value);
    }

    /**
     * 大于
     *
     * @param field
     * @param value
     * @return
     */
    public PageParms addGreaterThan(String field, Object value) {

        return addLogic(field, GREATER_THAN, value);
    }

    public PageParms addGreaterThanNotNull(String field, Object value) {

        return addLogicNotNull(field, GREATER_THAN, value);
    }

    /**
     * 大于等于
     *
     * @param field
     * @param value
     * @return
     */
    public PageParms addGreaterEqual(String field, Object value) {

        return addLogic(field, GREATER_EQUAL, value);
    }

    public PageParms addGreaterEqualNotNull(String field, Object value) {

        return addLogicNotNull(field, GREATER_EQUAL, value);
    }

    /**
     * 不等
     *
     * @param field
     * @param value
     * @return
     */
    public PageParms addNotEqual(String field, Object value) {

        return addLogic(field, NOT_EQUAL, value);
    }

    public PageParms addNotEqualNotNull(String field, Object value) {

        return addLogicNotNull(field, NOT_EQUAL, value);
    }

    /**
     * 等于
     *
     * @param field
     * @param value
     * @return
     */
    public PageParms addEqual(String field, Object value) {

        return addLogic(field, EQUAL, value);
    }

    public PageParms addEqualNotNull(String field, Object value) {

        return addLogicNotNull(field, EQUAL, value);
    }

    /**
     * 添加扩展参数
     *
     * @param key
     * @param value
     * @return
     */
    public PageParms addExt(String key, Object value) {
        put(key, value);
        return this;
    }

    public PageParms addExtNotNull(String key, Object value) {
        if (isEmpty(value))
            return this;
        put(key, value);
        return this;
    }

    /**
     * 自定查询类型
     *
     * @param field
     * @param logic 查询类型
     * @param value
     * @return
     */
    public PageParms addLogic(String field, String logic, Object value) {

        return addFilters(field + logic + value);
    }

    public PageParms addLogicNotNull(String field, String logic, Object value) {

        if (isEmpty(value))
            return this;
        return addLogic(field, logic, value);
    }

    /**
     * 添加或查询
     *
     * @param field
     * @param value
     * @param group
     * @return
     */
    public PageParms addGroup(String field, Object value, String group) {

        return addGroup(field, EQUAL, value, group);
    }

    public PageParms addGroupNotNull(String field, Object value, String group) {

        return addGroupNotNull(field, EQUAL, value, group);
    }

    public PageParms addGroup(String field, String logic, Object value, String group) {

        return addFilters(field + logic + value + " " + group);
    }

    public PageParms addGroupNotNull(String field, String logic, Object value, String group) {

        if (isEmpty(value))
            return this;
        return addGroup(field, logic, value, group);
    }


    public PageParms addFilters(String filters) {

        this.filters += filters + ";";
        return putFilters(this.filters);
    }

    public PageParms setSorts(String sorts) {

        this.sorts = sorts;
        put("sorts", this.sorts);
        return this;
    }

    private PageParms putFilters(String filters) {

        put("filters", filters);
        return this;
    }

    private void setValues() {

        put("fields", fields);
        put("filters", filters);
        put("sorts", sorts);
        put("size", rows);
        put("page", page);
    }

    private boolean isEmpty(Object value) {

        return StringUtils.isEmpty(value);
    }
}
