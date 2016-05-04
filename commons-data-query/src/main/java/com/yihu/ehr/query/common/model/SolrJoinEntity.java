package com.yihu.ehr.query.common.model;


import com.yihu.ehr.query.common.enums.Logical;

import java.util.List;

/**
 * Created by hzp on 2015/11/17.
 */
public class SolrJoinEntity {
    private String fromIndex;
    private String fromCol;
    private String toCol;
    private List<QueryCondition> conditions; //过滤条件

    /**
     * 构造函数
     */
    public SolrJoinEntity(String fromIndex, String fromCol, String toCol, List<QueryCondition> conditions)
    {
        this.fromIndex = fromIndex;
        this.fromCol = fromCol;
        this.toCol = toCol;
        this.conditions = conditions;
    }

    /**
     * 构造函数
     */
    public SolrJoinEntity(String fromCol, String toCol, List<QueryCondition> conditions)
    {
        this.fromCol = fromCol;
        this.toCol = toCol;
        this.conditions = conditions;
    }

    /**
     *join条件转字符串
     */
    public String toString()
    {
        if(!fromCol.equals("") && !toCol.equals(""))
        {
            String re = "{!join ";
            if(fromIndex!=null && !fromIndex.equals(""))
            {
                re += " fromIndex="+fromIndex;
            }
            re +=" from="+fromCol+" to="+toCol+"}";
            if(conditions!=null && conditions.size()>0)
            {
                String conditionString ="";
                for(QueryCondition condition :conditions)
                {
                    if(!conditionString.equals(""))
                    {
                        switch (condition.getLogical())
                        {
                            case Logical.AND:
                                conditionString+=" AND ";
                                break;
                            case Logical.OR:
                                conditionString+=" OR ";
                                break;
                            case Logical.NOT:
                                conditionString+=" NOT ";
                                break;
                        }
                    }

                    conditionString+=condition.toString() +" ";
                }
            }
            else {
                re +="*:* ";
            }
            return re;
        }
        else{
            return "";
        }
    }

}
