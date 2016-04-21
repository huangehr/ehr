package com.yihu.ehr.resource.model;


import com.yihu.ehr.resource.dao.BaseDao;


/**
 * Created by hzp on 2016/4/21.
 * 资源列表
 */
public class RsResources extends BaseDao {

    private String id;
    private String code;
    private String name;
    private String categoryId;
    private String type;
    private String relatedAction;
    private String relatedDatasource;
    private String relatedDatasets;
    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRelatedAction() {
        return relatedAction;
    }

    public void setRelatedAction(String relatedAction) {
        this.relatedAction = relatedAction;
    }

    public String getRelatedDatasource() {
        return relatedDatasource;
    }

    public void setRelatedDatasource(String relatedDatasource) {
        this.relatedDatasource = relatedDatasource;
    }

    public String getRelatedDatasets() {
        return relatedDatasets;
    }

    public void setRelatedDatasets(String relatedDatasets) {
        this.relatedDatasets = relatedDatasets;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
