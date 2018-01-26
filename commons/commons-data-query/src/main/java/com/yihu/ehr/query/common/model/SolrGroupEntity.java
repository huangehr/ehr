package com.yihu.ehr.query.common.model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * solr分组聚合条件
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
    private Object gap; // 分组间隔，用于范围统计
    private Map<String, String> groupCondition = new LinkedHashMap<>(); // 分组条件

    public SolrGroupEntity(String groupField) {
        this.groupField = groupField;
        this.type = GroupType.FIELD_VALUE;
        this.gap = null;
        this.groupCondition = new HashMap<String, String>();
    }

    public SolrGroupEntity(String groupField, Map<String, String> groupCondition) {
        this.groupField = groupField;
        this.type = GroupType.FIELD_VALUE;
        this.gap = null;
        this.groupCondition = groupCondition;
    }

    public SolrGroupEntity(String groupField, GroupType type) {
        this.groupField = groupField;
        this.type = GroupType.FIELD_VALUE;
        this.gap = null;
    }

    public SolrGroupEntity(String groupField, GroupType type, Object gap) {
        this.groupField = groupField;
        this.type = type;
        this.gap = gap;
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

    public Object getGap() {
        return gap;
    }

    public void setGap(Object gap) {
        this.gap = gap;
    }

    public Map<String, String> getGroupCondition() {
        return groupCondition;
    }

    public void putGroupCondition(String key, String condition) {
        this.groupCondition.put(key, condition);
    }

}