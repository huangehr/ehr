package com.yihu.ehr.agModel.resource;

import java.io.Serializable;
import java.util.List;

/**
 * 资源报表监测分类 model
 *
 * @author janseny
 * @date 2017/11/7 17:20
 */
public class RsReportMonitorTypeModel implements Serializable {

    private Integer id; // 主键
    private String name; // 名称
    private String note; // 备注

    private List<RsReportModel> reportModelList;

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

    public List<RsReportModel> getReportModelList() {
        return reportModelList;
    }

    public void setReportModelList(List<RsReportModel> reportModelList) {
        this.reportModelList = reportModelList;
    }
}
