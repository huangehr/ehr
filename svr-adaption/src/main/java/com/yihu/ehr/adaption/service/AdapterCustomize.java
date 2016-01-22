package com.yihu.ehr.adaption.service;

/** 适配定制对象
 * Created by zqb on 2015/11/2.
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
