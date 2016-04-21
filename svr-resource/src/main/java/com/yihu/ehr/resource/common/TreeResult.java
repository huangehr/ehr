package com.yihu.ehr.resource.common;

import java.io.Serializable;

/**
 * 树形数据对象
 * Created by hzp on 20160401
 */
public class TreeResult extends Result implements Serializable {
    String id;//节点IP
    String pid;//父节点IP
    String text;//显示的名字
    String code;//1是类别 2是接口
    String type;//类别
    String icon;//图标类

    public String getId() {
        return id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    public String getType() {
        return type;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setType(String type) {
        this.type = type;
    }


}
