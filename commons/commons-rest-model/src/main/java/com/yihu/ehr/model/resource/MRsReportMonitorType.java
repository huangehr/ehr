package com.yihu.ehr.model.resource;

import java.io.Serializable;
import java.util.List;

/**
 * 资源报表监测分类
 *
 * @author janseny
 * @created 2017年11月7日15:11:40
 */
public class MRsReportMonitorType implements Serializable {

    private Integer id; // 主键
    private String name; // 名称
    private String note; // 备注

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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
