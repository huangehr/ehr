package com.yihu.ehr.agModel.resource;

/**
 * Created by lyr on 2016/5/4.
 */
public class RsDimensionModel {
    private String id;
    private String code;
    private String name;
    private String categoryId;
    private String type;
    private String dictCode;
    private String description;

    public String getId(){
        return id;
    }
    public void setId(String id){
        this.id = id;
    }

    public String getCode(){
        return code;
    }
    public void setCode(String code)
    {
        this.code = code;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    public String getCategoryId()
    {
        return categoryId;
    }
    public void setCategoryId(String categoryId)
    {
        this.categoryId = categoryId;
    }

    public String getType(){
        return type;
    }
    public void setType(String type){
        this.type = type;
    }

    public String getDictCode()
    {
        return dictCode;
    }
    public void setDictCode(String dictCode){
        this.dictCode = dictCode;
    }

    public String getDescription(){
        return description;
    }
    public void setDescription(String description){
        this.description =  description;
    }
}
