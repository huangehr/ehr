package com.yihu.ehr.agModel.resource;

import java.io.Serializable;

/**
 * 资源报表视图配置 model
 *
 * @author 张进军
 * @created 2017.9.1 16:54
 */
public class RsReportViewModel implements Serializable {

    private Integer id; // 主键
    private Integer reportId; // 资源报表ID
    private String resourceId; // 视图ID
    private String resourceName; // 视图名称

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

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }
}
