package com.yihu.ehr.model.resource;

import java.util.List;
import java.util.Objects;

/**
 * Created by hzp on 2016/5/4.
 * 资源类别
 */
public class MRsCategory  {

    private String id;
    private String name;
    private String pid;
    private String description;
    private List<MRsResources> resourcesList;


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

    public List<MRsResources> getResourcesList() {
        return resourcesList;
    }

    public void setResourcesList(List<MRsResources> resourcesList) {
        this.resourcesList = resourcesList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MRsCategory that = (MRsCategory) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
