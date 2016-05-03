package com.yihu.ehr.query.common.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hzp on 2015/11/17.
 */
public class QueryEntity {
    private String tableName;
    private boolean isPage = false;
    private Integer page=0;
    private Integer rows=50; //默认50
    private String fields = "*"; //查询字段，逗号分隔
    private String sort;
    private List<QueryCondition> conditions; //过滤条件

    /**
     * 构造函数
     */
    public QueryEntity(String tableName)
    {
        this.tableName = tableName;
        this.conditions = new ArrayList<QueryCondition>();
    }

    /**
     * 构造函数
     */
    public QueryEntity(String tableName,int page, int rows)
    {
        this.tableName = tableName;
        this.isPage = true;
        this.page = page;
        this.rows = rows;
        this.conditions = new ArrayList<QueryCondition>();
    }

    /*************************************************************************************/
    public List<QueryCondition> getConditions() {
        return conditions;
    }

    public String getTableName() {
        return tableName;
    }

    public String getSort() {
        return sort;
    }

    public int getPage() {
        return page;
    }

    public int getRows() {
        return rows;
    }

    public String getFields() {
        return fields;
    }

    public boolean isPage() {
        return isPage;
    }

    /************************************************************************************/
    /**
     * 设置排序
     */
    public void setSort(String sort){
        this.sort = sort;
    }

    /**
     * 设置查询字段
     * @param fields
     */
    public void setFields(String fields) {
        this.fields = fields;
    }

    /**
     * 新增查询条件
     */
    public void addCondition(String field,String keyword){
        QueryCondition condition = new QueryCondition(field,keyword);

        addCondition(condition);
    }

    /**
     * 新增查询条件
     */
    public void addCondition(QueryCondition condition){
        conditions.add(condition);
    }

}
