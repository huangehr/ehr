package com.yihu.ehr.resource.model;

import com.yihu.ehr.constants.SystemDictId;
import org.hibernate.annotations.Formula;

import javax.persistence.*;

/**
 * 资源报表 entity
 *
 * @author 张进军
 * @created 2017.8.15 19:18
 */
@Entity
@Table(name = "rs_report")
public class RsReport {

    private Integer id; // 主键
    private String code; // 报表编码
    private String name; // 报表名称
    private Integer reportCategoryId; // 资源报表分类ID
    private String status; // 报表状态
    private String remark; // 备注
    private String templatePath; // 报表模版路径

    // 临时字段
    private String reportCategory; // 报表分类名称
    private String statusName; // 报表状态名称

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "code")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "REPORT_CATEGORY_ID")
    public Integer getReportCategoryId() {
        return reportCategoryId;
    }

    public void setReportCategoryId(Integer reportCategoryId) {
        this.reportCategoryId = reportCategoryId;
    }

    @Column(name = "STATUS")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Column(name = "remark")
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Column(name = "TEMPLATE_PATH")
    public String getTemplatePath() {
        return templatePath;
    }

    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    @Formula("(SELECT rs.name FROM rs_report r LEFT JOIN rs_report_category rs ON rs.id = r.report_category_id WHERE r.id = id )")
    public String getReportCategory() {
        return reportCategory;
    }

    public void setReportCategory(String reportCategory) {
        this.reportCategory = reportCategory;
    }

    @Formula("(SELECT de.value FROM rs_report r LEFT JOIN system_dict_entries de ON de.dict_id = " + SystemDictId.RsReportStatus + " AND de.code = r.status WHERE r.id = id )")
    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
}
