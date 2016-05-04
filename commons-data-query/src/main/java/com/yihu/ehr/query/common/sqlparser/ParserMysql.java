package com.yihu.ehr.query.common.sqlparser;

import com.yihu.ehr.query.common.model.QueryCondition;
import com.yihu.ehr.query.common.model.QueryEntity;
import com.yihu.ehr.query.common.enums.Logical;
import com.yihu.ehr.query.common.enums.Operation;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Mysql的sql解析
 * Created by hzp on 2015/11/30.
 */
public class ParserMysql extends ParserSql {

    /**
     * 获取keyword转sql
     * @param keyword
     * @return
     */
    @Override
    public String getKeywordSql(Object keyword){
        if (keyword instanceof Date) {
            String dateFormat = "yyyy-MM-dd HH:mm:ss";
            Date d = (Date) keyword;
            SimpleDateFormat format = new SimpleDateFormat(dateFormat);
            return "date_format('"+ format .format(d)+"','"+dateFormat+"')";
        }
        else
        {
            return "'"+ keyword +"'";
        }
    }

    /**
     * 条件转换
     * @return
     */
    @Override
    public String getConditionSql(List<QueryCondition> conditions){
        String conditionSql = "";
        for(QueryCondition qc : conditions){
            if(qc.getLogical().equals(Logical.OR))
            {
                conditionSql += " or ";
            }
            else{
                conditionSql += " and ";
            }

            String operation = qc.getOperation();

            if(operation.equals(Operation.IN)  || operation.equals(Operation.NIN))
            {
                Object[] keys = qc.getKeywords();
                String keywords = "";
                if(keys!=null&&keys.length>0)
                {
                    for (Object key :keys)
                    {
                        keywords +=  "'"+key+"',";
                    }
                    keywords = keywords.substring(0,keywords.length()-1);
                }


                conditionSql += qc.getField() + operation + keywords;
            }
            else if(operation.equals(Operation.LIKE))
            {
                conditionSql += qc.getField() + operation + "'%"+qc.getKeyword()+"%'";
            }
            else{
                conditionSql += qc.getField() + operation + getKeywordSql(qc.getKeyword());
            }
        }


        return conditionSql;
    }

    /**
     * 转换成sql语句
     */
    @Override
    public String getSql(QueryEntity qe){
        String fields = qe.getFields();
        String tableName = qe.getTableName();
        String sort = qe.getSort();
        //查询条件
        String conditionSql = "";
        List<QueryCondition> conditions = qe.getConditions();
        if(conditions!=null && conditions.size()>0)
        {
            conditionSql = getConditionSql(conditions);

        }

        String sql = "select "+fields+" from "+ tableName +" where 1=1" + conditionSql;
        //判断是否排序
        if(sort!=null &&sort.length()>0)
        {
            sql+= " ORDER BY " + sort;
        }

        //判断是否分页
        if(qe.isPage())
        {
            int page = qe.getPage();
            int rows = qe.getRows();
            sql = getPageSql(sql,page,rows);
        }

        return sql;
    }

    /**
     * 转换成获取count的sql语句
     */
    @Override
    public String getCountSql(QueryEntity qe){

        String tableName = qe.getTableName();
        //查询条件
        String conditionSql = "";
        List<QueryCondition> conditions = qe.getConditions();
        if(conditions!=null && conditions.size()>0)
        {
            conditionSql = getConditionSql(conditions);
        }

        return "select count(1) from "+ tableName +" where 1=1" + conditionSql;
    }


    /**
     * 通过sql语句再分页
     * @return
     */
    @Override
    public String getPageSql(String sql,int page,int rows)
    {
        if(rows < 0) rows = 10;
        if(rows >100) rows = 100;
        if(page <0) page = 1;
        int start= (page-1) * rows;
        return sql +" LIMIT "+start+","+rows;
    }


}
