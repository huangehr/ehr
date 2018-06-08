package com.yihu.ehr.model.resource;

/**
 * Created by hzp on 2016/5/4.
 * 资源列表
 */
public class MRsResources {
    private String id;
    private String code;
    private String name;
    private String categoryId;
    private String rsInterface;
    private String grantType;
    private String description;
    private Integer dataSource;
    private String echartType;
    private String dimension;
    // 计量数值
    private String dataMeasurement;
    // 单位
    private String dataUnit;
    // 单位放置位置
    private String dataPosition;


    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
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

    public String getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getRsInterface()
    {
        return rsInterface;
    }
    public void setRsInterface(String rsInterface)
    {
        this.rsInterface = rsInterface;
    }

    public String getGrantType()
    {
        return grantType;
    }
    public void setGrantType(String grantType)
    {
        this.grantType = grantType;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDataSource() {
        return dataSource;
    }

    public void setDataSource(Integer dataSource) {
        this.dataSource = dataSource;
    }

    public String getEchartType() {
        return echartType;
    }

    public void setEchartType(String echartType) {
        this.echartType = echartType;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public String getDataMeasurement() {
        return dataMeasurement;
    }

    public void setDataMeasurement(String dataMeasurement) {
        this.dataMeasurement = dataMeasurement;
    }

    public String getDataUnit() {
        return dataUnit;
    }

    public void setDataUnit(String dataUnit) {
        this.dataUnit = dataUnit;
    }

    public String getDataPosition() {
        return dataPosition;
    }

    public void setDataPosition(String dataPosition) {
        this.dataPosition = dataPosition;
    }
}
