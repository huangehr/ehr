package com.yihu.ehr.agModel.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lincl
 * @version 1.0
 * @created 2016/5/31
 */
public class TreeModel {
    String id = "";
    String name = "";
    List children;
    Map otherPro;

    public TreeModel(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public TreeModel(String id, String name, List children) {
        this.id = id;
        this.name = name;
        this.children = children;
    }

    public TreeModel() {

    }

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

    public List getChildren() {
        return children;
    }

    public void setChildren(List children) {
        this.children = children;
    }

    public Map getOtherPro() {
        return otherPro;
    }

    public void setOtherPro(Map otherPro) {
        this.otherPro = otherPro;
    }

    public void addOtherPro(String k, Object v){
        if(this.otherPro==null)
            this.otherPro = new HashMap<>();
        this.otherPro.put(k, v);
    }
}
