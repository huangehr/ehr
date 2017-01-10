package com.yihu.ehr.resource.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by lyr on 2016/4/25.
 */
@Entity
@Table(name="rs_dimension_category")
public class RsDimensionCategory {

    private String id;
    private String name;
    private String pid;
    private String description;

    @Id
    @GeneratedValue(generator="Generator")
    @GenericGenerator(name="Generator",strategy = "assigned")
    @Column(name="id",unique = true,nullable = false)
    public String getId()
    {
        return id;
    }
    public void setId(String id)
    {
        this.id = id;
    }

    @Column(name="name",nullable = false)
    public String getName()
    {
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    @Column(name="pid")
    public String getPid()
    {
        return pid;
    }
    public void setPid(String pid)
    {
        this.pid = pid;
    }

    @Column(name="description")
    public String getDescription()
    {
        return description;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }
}
