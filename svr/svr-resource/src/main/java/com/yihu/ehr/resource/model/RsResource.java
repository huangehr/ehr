package com.yihu.ehr.resource.model;

import com.yihu.ehr.entity.BaseAssignedEntity;

import javax.persistence.*;

/**
 * Entity - 资源
 * Created by hzp on 2016/4/21.
 * Modify by Progr1mmer 2017/11/20
 */
@Entity
@Table(name = "rs_resource")
@Access(value = AccessType.PROPERTY)
public class RsResource extends BaseAssignedEntity {

    // 编码
    private String code;
    // 名称
    private String name;
    // 分类ID
    private String categoryId;
    // 数据接口
    private String rsInterface;
    // 授权类型 （0 开放 1 授权）
    private String grantType;
    // 描述
    private String description;
    // 资源类型
    private Integer dataSource;
    //指标视图展示类型
    private String echartType;

    @Column(name="code",nullable = false)
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    @Column(name="name",nullable = false)
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Column(name="category_id",nullable=false)
    public String getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    @Column(name="rs_interface")
    public String getRsInterface()
    {
        return rsInterface;
    }
    public void setRsInterface(String rsInterface)
    {
        this.rsInterface = rsInterface;
    }

    @Column(name="grant_type")
    public String getGrantType()
    {
        return grantType;
    }
    public void setGrantType(String grantType)
    {
        this.grantType = grantType;
    }

    @Column(name="description")
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name="data_source")
    public Integer getDataSource() {
        return dataSource;
    }

    public void setDataSource(Integer dataSource) {
        this.dataSource = dataSource;
    }

    @Column(name="echart_type")
    public String getEchartType() {
        return echartType;
    }

    public void setEchartType(String echartType) {
        this.echartType = echartType;
    }
}
