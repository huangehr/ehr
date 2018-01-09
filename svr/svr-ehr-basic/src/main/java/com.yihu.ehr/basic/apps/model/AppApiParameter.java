package com.yihu.ehr.basic.apps.model;

import javax.persistence.*;

/**
 * apps_api_parameter对象。
 *
 * @author linzhuo
 * @version 1.0
 * @created 2016年7月7日17:45:30
 */

@Entity
@Table(name = "apps_api_parameter")
public class AppApiParameter {

    private int id;
    private String name;
    private String type;
    private String dataType;
    private String description;
    private String required;
    private int appApiId;
    private String memo;
    private String defaultValue;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "name", nullable = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @Column(name = "description", nullable = true)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    @Column(name = "type", nullable = true)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(name = "data_type", nullable = true)
    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    @Column(name = "required", nullable = true)
    public String getRequired() {
        return required;
    }

    public void setRequired(String required) {
        this.required = required;
    }

    @Column(name = "app_api_id", nullable = true)
    public int getAppApiId() {
        return appApiId;
    }

    public void setAppApiId(int appApiId) {
        this.appApiId = appApiId;
    }
    @Column(name = "memo", nullable = true)
    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    @Column(name = "default_value", nullable = true)
    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}