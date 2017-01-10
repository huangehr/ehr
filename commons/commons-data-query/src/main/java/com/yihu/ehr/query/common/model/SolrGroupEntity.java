package com.yihu.ehr.query.common.model;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义分组条件
 * Created by hzp on 2015/11/17.
 */
public class SolrGroupEntity {

    private String groupField; //分组字段名
    private Map<String,String> groupCondition; //分组条件

    /**
     * 构造函数
     */
    public SolrGroupEntity(String groupField)
    {
        this.groupField = groupField;
        this.groupCondition= new HashMap<String,String>();
    }

    /**
     * 构造函数
     */
    public SolrGroupEntity(String groupField, Map<String, String> groupCondition)
    {
        this.groupField = groupField;
        this.groupCondition= groupCondition;
    }

    /**
     * 获取分组字段名
     * @return
     */
    public String getGroupField() {
        return groupField;
    }

    /**
     * 获取分组条件
     * @return
     */
    public Map<String, String> getGroupCondition() {
        return groupCondition;
    }

    /**
     * 添加分组条件项
     */
    public void putGroupCondition(String key,String condition){
        this.groupCondition.put(key,condition);
    }
}