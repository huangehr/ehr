package com.yihu.ehr.resource.model;

import org.hibernate.annotations.Formula;

import javax.persistence.*;

/**
 * 资源监测类型报表配置 entity
 *
 * @author janseny
 * @created 2017.11.8 14:05
 */
@Entity
@Table(name = "rs_monitor_type_report")
public class RsMonitorTypeReport {

    private Integer id; // 主键
    private Integer reportId; // 资源报表ID
    private Integer rsReoportMonitorTypeId; // 资源报表监测类型ID

    // 临时字段
    private String reportName; // 报表名称

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "report_id")
    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    @Column(name = "rs_reoport_monitor_type_id")
    public Integer getRsReoportMonitorTypeId() {
        return rsReoportMonitorTypeId;
    }

    public void setRsReoportMonitorTypeId(Integer rsReoportMonitorTypeId) {
        this.rsReoportMonitorTypeId = rsReoportMonitorTypeId;
    }

    @Formula("(SELECT rr.name FROM rs_monitor_type_report rmtr LEFT JOIN rs_report rr ON rr.id = rmtr.report_id WHERE rmtr.ID = id )")
    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }





}
