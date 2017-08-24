package com.yihu.ehr.agModel.resource;

import java.util.List;

/**
 * Created by hzp on 2016/5/4.
 * 资源类别
 */
public class RsCategoryModel {

    private String id;
    private String name;
    private String pid;
    private String pname;
    private String description;

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
