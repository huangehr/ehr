package com.yihu.ehr.model.quality;

import java.io.Serializable;

/**
 * 数据质量-数据集预警值
 * @author yeshijie on 2018/5/29.
 */
public class MDqDatasetWarning implements Serializable {


    private Long id;
    private String orgCode;//机构代码
    private String type;//类型(1平台接收，2平台上传)
    private String code;//数据集编码
    private String name;//数据集名称

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
}
