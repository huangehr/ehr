package com.yihu.ehr.query.common.model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 自定义分组条件
 * Created by hzp on 2015/11/17.
 */
public class SolrGroupEntity {

    // 分组统计类型
    public static enum GroupType {
        FIELD_VALUE, // 按字段值统计
        DATE_RANGE, // 日期范围统计
        NUMERIC_RANGE // 数值范围统计
    }

    private String groupField; // 分组字段名
    private GroupType type; // 分组统计类型
    private Map<String, String> groupCondition = new LinkedHashMap<>(); // 分组条件

    public SolrGroupEntity(String groupField) {
        this.groupField = groupField;
        this.type = GroupType.FIELD_VALUE;
        this.groupCondition = new HashMap<String, String>();
    }

    public SolrGroupEntity(String groupField, Map<String, String> groupCondition) {
        this.groupField = groupField;
        this.type = GroupType.FIELD_VALUE;
        this.groupCondition = groupCondition;
    }

    public SolrGroupEntity(String groupField, GroupType type, Map<String, String> groupCondition) {
        this.groupField = groupField;
        this.type = type;
        this.groupCondition = groupCondition;
    }

    public String getGroupField() {
        return groupField;
    }

    public void setGroupField(String groupField) {
        this.groupField = groupField;
    }

    public GroupType getType() {
        return type;
    }

    public void setType(GroupType type) {
        this.type = type;
    }

    public Map<String, String> getGroupCondition() {
        return groupCondition;
    }

    public void putGroupCondition(String key, String condition) {
        this.groupCondition.put(key, condition);
    }

}