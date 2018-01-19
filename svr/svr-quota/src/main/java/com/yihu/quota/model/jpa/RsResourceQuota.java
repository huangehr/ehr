package com.yihu.quota.model.jpa;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/12/19.
 */
@Entity
@Table(name="rs_resource_quota")
public class RsResourceQuota {
    private int id;
    private String resourceId;
    private String quotaTypeName;
    private String quotaCode;
    private int quotaChart;
    private int quotaId;
    private Integer pid;

    private List<RsResourceQuota> children;
    private List<Map<String, Object>> mapList;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "resource_id")
    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    @Column(name = "quota_type_name")
    public String getQuotaTypeName() {
        return quotaTypeName;
    }

    public void setQuotaTypeName(String quotaTypeName) {
        this.quotaTypeName = quotaTypeName;
    }

    @Column(name = "quota_code")
    public String getQuotaCode() {
        return quotaCode;
    }

    public void setQuotaCode(String quotaCode) {
        this.quotaCode = quotaCode;
    }

    @Column(name = "quota_chart")
    public int getQuotaChart() {
        return quotaChart;
    }

    public void setQuotaChart(int quotaChart) {
        this.quotaChart = quotaChart;
    }

    @Column(name = "quota_id")
    public int getQuotaId() {
        return quotaId;
    }

    public void setQuotaId(int quotaId) {
        this.quotaId = quotaId;
    }

    @Column(name = "pid")
    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    @Transient
    public List<RsResourceQuota> getChildren() {
        return children;
    }

    public void setChildren(List<RsResourceQuota> children) {
        this.children = children;
    }

    @Transient
    public List<Map<String, Object>> getMapList() {
        return mapList;
    }

    public void setMapList(List<Map<String, Object>> mapList) {
        this.mapList = mapList;
    }
}
