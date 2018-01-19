package com.yihu.ehr.basic.apps.model;

import javax.persistence.*;

/**
 * apps_api_response对象。
 *
 * @author linzhuo
 * @version 1.0
 * @created 2016年7月7日17:45:30
 */

@Entity
@Table(name = "apps_api_response")
public class AppApiResponse {

    private int id;
    private String name;
    private String dataType;
    private String description;
    private int appApiId;
    private String memo;

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

    @Column(name = "data_type", nullable = true)
    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
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

}