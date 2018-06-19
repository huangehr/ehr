package com.yihu.ehr.entity.quality;

import javax.persistence.*;

/**
 * 数据质量-数据集预警值
 * @author yeshijie on 2018/5/28.
 */
@Entity
@Table(name = "dq_dataset_warning", schema = "", catalog = "healtharchive")
public class DqDatasetWarning {


    private Long id;
    private String orgCode;//机构代码
    private String type;//类型(1平台接收，2平台上传)
    private String code;//数据集编码
    private String name;//数据集名称

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "org_code")
    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    @Column(name = "type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
}
