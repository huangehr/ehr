package com.yihu.ehr.resource.model;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;

/**
 * Created by lyr on 2016/4/25.
 */
@Entity
@Table(name="rs_dimension")
public class RsDimension {
    private String id;
    private String code;
    private String name;
    private String categoryId;
    private String type;
    private String dictCode;
    private String description;

    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name="Generator",strategy = "assigned")
    @Column(name="id",unique = true,nullable = false)
    public String getId(){
        return id;
    }
    public void setId(String id){
        this.id = id;
    }

    @Column(name="code",nullable = false)
    public String getCode(){
        return code;
    }
    public void setCode(String code)
    {
        this.code = code;
    }

    @Column(name="name",nullable = false)
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    @Column(name="category_id",nullable = false)
    public String getCategoryId()
    {
        return categoryId;
    }
    public void setCategoryId(String categoryId)
    {
        this.categoryId = categoryId;
    }

    @Column(name="type",nullable = false)
    public String getType(){
        return type;
    }
    public void setType(String type){
        this.type = type;
    }

    @Column(name="dict_code")
    public String getDictCode()
    {
        return dictCode;
    }
    public void setDictCode(String dictCode){
        this.dictCode = dictCode;
    }

    @Column(name="description")
    public String getDescription(){
        return description;
    }
    public void setDescription(String description){
        this.description =  description;
    }
}
