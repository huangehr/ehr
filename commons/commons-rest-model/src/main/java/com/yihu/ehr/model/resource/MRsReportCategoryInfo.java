package com.yihu.ehr.model.resource;

import java.io.Serializable;
import java.util.List;

/**
 * 资源报表分类
 *
 * @author
 * @created 2017.8.8 19:22
 */
public class MRsReportCategoryInfo implements Serializable {

    private Integer id; // 主键
    private Integer pid; // 父级ID
    private String code; // 编码
    private String name; // 名称
    private String remark; // 备注
    private String text; // 名称：树形下拉框使用
    private List<MRsReport> reportList; //该分类对应的报表
    private boolean flag;   //是否选中

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<MRsReport> getReportList() {
        return reportList;
    }

    public void setReportList(List<MRsReport> reportList) {
        this.reportList = reportList;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
