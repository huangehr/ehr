package com.yihu.ehr.query.common.sqlparser;

import com.yihu.ehr.query.common.model.QueryCondition;
import com.yihu.ehr.query.common.model.QueryEntity;

import java.util.List;

/**
 * sql解析基类
 * Created by hzp on 2015/11/30.
 */
public class ParserSql {


    /**
     * 获取keyword转sql
     * @param keyword
     * @return
     */
    public String getKeywordSql(Object keyword){
        return "";
    }

    /**
     * 条件转换
     * @return
     */
    public String getConditionSql(List<QueryCondition> conditions){
        return "";
    }

    /**
     * 转换成sql语句
     */
    public String getSql(QueryEntity qe){
        return "";
    }

    /**
     * 转换成获取count的sql语句
     */
    public String getCountSql(QueryEntity qe){
        return "";
    }

    /**
     * 转换成获取count的sql语句
     */
    public String getCountSql(String sql){
        return "select count(1) from ("+sql+") tc";
    }

    /**
     * sql语句添加分页
     */
    public String getPageSql(String sql,int page,int rows){
        return sql;
    }
}
