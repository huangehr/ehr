package com.yihu.ehr.adaption.model;

/**
 * 适配定制对象
 *
 * @author lincl
 * @version 1.0
 * @created 2016.2.3
 */
public class AdapterCustomize {
    String id;
    String pid;
    String text;
    Boolean ischecked;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Boolean getIschecked() {
        return ischecked;
    }

    public void setIschecked(Boolean ischecked) {
        this.ischecked = ischecked;
    }
}
