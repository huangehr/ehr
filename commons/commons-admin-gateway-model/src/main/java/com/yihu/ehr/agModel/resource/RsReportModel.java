package com.yihu.ehr.agModel.resource;

import java.io.Serializable;

/**
 * 资源报表 model
 *
 * @author 张进军
 * @created 2017.8.15 19:18
 */
public class RsReportModel implements Serializable {

    private Integer id; // 主键
    private String code; // 报表编码
    private String name; // 报表名称
    private Integer reportCategoryId; // 资源报表分类ID
    private String status; // 报表状态
    private String remark; // 备注
    private String templatePath; // 报表模版路径
    private String showType;    // 报表展示类型  1、图表  2、二维表
    private String position;    // 报表中视图和位置的关系 json串

    private String reportCategory; // 报表分类名称
    private String statusName; // 报表状态名称

    private boolean flag;   //界面显示，是否选中

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public Integer getReportCategoryId() {
        return reportCategoryId;
    }

    public void setReportCategoryId(Integer reportCategoryId) {
        this.reportCategoryId = reportCategoryId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTemplatePath() {
        return templatePath;
    }

    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    public String getShowType() {
        return showType;
    }

    public void setShowType(String showType) {
        this.showType = showType;
    }

    public String getReportCategory() {
        return reportCategory;
    }

    public void setReportCategory(String reportCategory) {
        this.reportCategory = reportCategory;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
