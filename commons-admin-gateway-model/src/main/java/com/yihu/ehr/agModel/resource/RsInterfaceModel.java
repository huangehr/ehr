package com.yihu.ehr.agModel.resource;

/**
 * Created by lyr on 2016/5/16.
 */
public class RsInterfaceModel {
    private String id;
    private String name;
    private String resourceInterface;
    private String paramDescription;
    private String resultDescription;
    private String description;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    public String getResourceInterface(){
        return resourceInterface;
    }
    public void setResourceInterface(String resourceInterface){
        this.resourceInterface = resourceInterface;
    }

    public String getParamDescription(){
        return paramDescription;
    }
    public void setParamDescription(String paramDescription) {
        this.paramDescription = paramDescription;
    }

    public String getResultDescription() {
        return resultDescription;
    }
    public void setResultDescription(String resultDescription) {
        this.resultDescription = resultDescription;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
