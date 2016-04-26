package com.yihu.ehr.solr.query;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hzp on 2015/11/17.
 */
public class HbaseQueryEntity {
    private String table;
    private int page;
    private int rows; //默认50
    private String sort;
    private List<HbaseQueryCondition> conditions; //过滤条件
    private List<HbaseQueryJoin> joins; //左关联条件

    public HbaseQueryEntity(String table,int page, int rows)
    {
        this.table = table;
        this.page = page;
        this.rows = rows;
        this.sort = null;
        this.conditions = new ArrayList<HbaseQueryCondition>();
        this.joins= new ArrayList<HbaseQueryJoin>();
    }

    /*************************************************************************************/
    public List<HbaseQueryCondition> getConditions() {
        return conditions;
    }

    public List<HbaseQueryJoin> getJoins() {
        return joins;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getSort() {
        return sort;
    }

    public long getPage() {
        return page;
    }

    public long getRows() {
        return rows;
    }


    /************************************************************************************/
    /**
     * 设置排序
     */
    public void setSort(String sort){
        this.sort = sort;
    }

    /**
     * 新增查询条件=
     */
    public void addCondition(String field,String keyword){
        HbaseQueryCondition condition = new HbaseQueryCondition(field,keyword);

        addCondition(condition);
    }

    /**
     * 新增查询条件
     */
    public void addCondition(HbaseQueryCondition condition){
        conditions.add(condition);
    }

    /**
     * 新增join条件
     */
    public void addJoin(){
        HbaseQueryJoin join = new HbaseQueryJoin("","",null);

        addJoin(join);
    }
    /**
     * 新增join条件
     */
    public void addJoin(HbaseQueryJoin join){
        joins.add(join);
    }


}
