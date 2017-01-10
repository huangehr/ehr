package com.yihu.ehr.model.resource;

/**
 * Created by lyr on 2016/5/4.
 */
public class MRsDimensionCategory {

    private String id;
    private String name;
    private String pid;
    private String description;

    public String getId()
    {
        return id;
    }
    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    public String getPid()
    {
        return pid;
    }
    public void setPid(String pid)
    {
        this.pid = pid;
    }

    public String getDescription()
    {
        return description;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }
}
