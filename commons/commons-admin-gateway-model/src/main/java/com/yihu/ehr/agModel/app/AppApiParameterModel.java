package com.yihu.ehr.agModel.app;

/**
 * AppApiParameter对象。
 *
 * @author linzhuo
 * @version 1.0
 * @created 2016年7月7日17:45:30
 */
public class AppApiParameterModel {

    private int id;
    private String name;
    private String type;
    private String typeName;
    private String dataType;
    private String dataTypeName;
    private String description;
    private String required;
    private String requiredName;
    private int appApiId;
    private String memo;
    private String defaultValue;

    public String getRequiredName() {
        return requiredName;
    }

    public void setRequiredName(String requiredName) {
        this.requiredName = requiredName;
    }

    public String getDataTypeName() {
        return dataTypeName;
    }

    public void setDataTypeName(String dataTypeName) {
        this.dataTypeName = dataTypeName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getRequired() {
        return required;
    }

    public void setRequired(String required) {
        this.required = required;
    }

    public int getAppApiId() {
        return appApiId;
    }

    public void setAppApiId(int appApiId) {
        this.appApiId = appApiId;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}