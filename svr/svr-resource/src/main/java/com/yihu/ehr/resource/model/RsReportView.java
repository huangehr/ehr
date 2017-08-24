package com.yihu.ehr.resource.model;

import javax.persistence.*;

/**
 * 资源报表视图配置 entity
 *
 * @author 张进军
 * @created 2017.8.22 14:05
 */
@Entity
@Table(name = "rs_report_view")
public class RsReportView {

    private Integer id; // 主键
    private Integer reportId; // 资源报表ID
    private String resourceId; // 视图ID

    // 临时字段
    private String resourceName; // 视图名称

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "REPORT_ID")
    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    @Column(name = "RESOURCE_ID")
    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    @Transient
    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }
}
