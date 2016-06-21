package com.yihu.ehr.resource.model;

import javax.persistence.*;

/**
 * 资源字典
 *
 * Created by lyr on 2016/5/13.
 */
@Entity
@Table(name="rs_dictionary")
public class RsDictionary {
    private String id;
    private String code;
    private String name;
    private String description;


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "code",nullable = false)
    public String getCode()
    {
        return code;
    }
    public void setCode(String code)
    {
        this.code = code;
    }

    @Column(name="name",nullable = false)
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
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
