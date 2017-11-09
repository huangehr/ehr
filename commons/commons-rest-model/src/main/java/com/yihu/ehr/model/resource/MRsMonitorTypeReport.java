package com.yihu.ehr.model.resource;

import java.io.Serializable;

/**
 * 监测类型报表配置
 *
 * @author janseny
 * @created 2017.11.8 15:05
 */
public class MRsMonitorTypeReport implements Serializable {

    private Integer id; // 主键
    private Integer reportId; // 资源报表ID
    private Integer rsReoportMonitorTypeId;// 资源报表监测类型ID
    private String reportName; // 报表名称
    private String rsReoportMonitorTypeName; // 资源报表监测类型名称

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    public Integer getRsReoportMonitorTypeId() {
        return rsReoportMonitorTypeId;
    }

    public void setRsReoportMonitorTypeId(Integer rsReoportMonitorTypeId) {
        this.rsReoportMonitorTypeId = rsReoportMonitorTypeId;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getRsReoportMonitorTypeName() {
        return rsReoportMonitorTypeName;
    }

    public void setRsReoportMonitorTypeName(String rsReoportMonitorTypeName) {
        this.rsReoportMonitorTypeName = rsReoportMonitorTypeName;
    }
}
