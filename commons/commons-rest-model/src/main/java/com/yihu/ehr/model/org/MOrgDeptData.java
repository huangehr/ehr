package com.yihu.ehr.model.org;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wxw on 2017/10/9.
 */
public class MOrgDeptData implements Serializable {
    private Integer id;
    private String name;
    private List<MOrgDeptData> children;
    private boolean checked;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MOrgDeptData> getChildren() {
        return children;
    }

    public void setChildren(List<MOrgDeptData> children) {
        this.children = children;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
