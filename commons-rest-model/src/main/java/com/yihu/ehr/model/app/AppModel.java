package com.yihu.ehr.model.app;

import java.util.List;

/**
 * Created by cws on 2015/8/16.
 */
public class AppModel {

    String id;
    String name;
    String catalog;
    String status;
    List<AppDetailModel> appDetailModels;

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

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<AppDetailModel> getAppDetailModels() {
        return appDetailModels;
    }

    public void setAppDetailModels(List<AppDetailModel> appDetailModels) {
        this.appDetailModels = appDetailModels;
    }
}
