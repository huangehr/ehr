package com.yihu.ehr.model.app;

import java.io.Serializable;

/**
 * MAppApiResponse对象。
 *
 * @author linzhuo
 * @version 1.0
 * @created 2016年7月7日17:45:30
 */
public class MAppApiResponse implements Serializable {

    private int id;
    private String name;
    private String dataType;
    private String description;
    private int appApiId;
    private String memo;

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

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
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

}