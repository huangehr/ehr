package com.yihu.ehr.solr.query;

import java.util.List;

/**
 * Created by hzp on 2015/11/17.
 */
public class HbaseQueryJoin {
    private String fromIndex;
    private String fromCol;
    private String toCol;
    private List<HbaseQueryCondition> conditions; //过滤条件

    /**
     * 构造函数
     */
    public HbaseQueryJoin(String fromIndex, String fromCol, String toCol, List<HbaseQueryCondition> conditions)
    {
        this.fromIndex = fromIndex;
        this.fromCol = fromCol;
        this.toCol = toCol;
        this.conditions = conditions;
    }

    /**
     * 构造函数
     */
    public HbaseQueryJoin(String fromCol, String toCol, List<HbaseQueryCondition> conditions)
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
                for(HbaseQueryCondition condition :conditions)
                {
                    if(!conditionString.equals(""))
                    {
                        switch (condition.getLogical())
                        {
                            case AND:
                                conditionString+=" AND ";
                                break;
                            case OR:
                                conditionString+=" OR ";
                                break;
                            case NOT:
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
