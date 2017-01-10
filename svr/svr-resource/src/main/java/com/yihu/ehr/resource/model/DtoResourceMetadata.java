package com.yihu.ehr.resource.model;


/**
 * Created by hzp on 2016/5/14.
 */
public class DtoResourceMetadata extends RsMetadata {

    private String groupType; //分组类型
    private String groupData; //分组数据
    private String dimensionValue; //维度范围

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public String getGroupData() {
        return groupData;
    }

    public void setGroupData(String groupData) {
        this.groupData = groupData;
    }

    public String getDimensionValue() {
        return dimensionValue;
    }

    public void setDimensionValue(String dimensionValue) {
        this.dimensionValue = dimensionValue;
    }
}
