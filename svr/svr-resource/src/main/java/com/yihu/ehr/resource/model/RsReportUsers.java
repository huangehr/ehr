package com.yihu.ehr.resource.model;

import org.hibernate.annotations.Formula;

import javax.persistence.*;

/**
 * Created by wxw on 2018/7/31.
 */
@Entity
@Table(name = "rs_report_users")
public class RsReportUsers {

    private Integer id; // 主键
    private String reportCode;  // 报表编码
    private String userId;  // 用户id
    private String sortNo;  // 排序

    // 临时字段
    private String reportName;
    private String showType;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "report_code")
    public String getReportCode() {
        return reportCode;
    }

    public void setReportCode(String reportCode) {
        this.reportCode = reportCode;
    }

    @Column(name = "user_id")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Column(name = "sort_no")
    public String getSortNo() {
        return sortNo;
    }

    public void setSortNo(String sortNo) {
        this.sortNo = sortNo;
    }

    @Formula("(SELECT r.NAME from rs_report r RIGHT JOIN rs_report_users ru on r.CODE = ru.report_code WHERE ru.id = id )")
    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    @Formula("(SELECT r.show_type from rs_report r RIGHT JOIN rs_report_users ru on r.CODE = ru.report_code WHERE ru.id = id )")
    public String getShowType() {
        return showType;
    }

    public void setShowType(String showType) {
        this.showType = showType;
    }
}
