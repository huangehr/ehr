package com.yihu.ehr.resource.model;


import com.yihu.ehr.resource.dao.BaseDao;
import org.springframework.stereotype.Service;


/**
 * Created by hzp on 2016/4/20.
 * 资源类别
 */
public class RsCategory extends BaseDao {

    private String id;
    private String name;
    private String pid;
    private String description;

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
