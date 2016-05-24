package com.yihu.ehr.agModel.resource;

/**
 * Created by lyr on 2016/5/4.
 */
public class RsAppResourceMetadataModel {
    private String id;
    private String AppResourceId;
    private String ResourceMetadataId;
    private String DimensionId;
    private String DimensionValue;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getAppResourceId() {
        return AppResourceId;
    }
    public void setAppResourceId(String appResourceId) {
        AppResourceId = appResourceId;
    }

    public String getMetadataId() {
        return ResourceMetadataId;
    }
    public void setMetadataId(String ResourceMetadataId) {
        ResourceMetadataId = ResourceMetadataId;
    }

    public String getDimensionId() {
        return DimensionId;
    }
    public void setDimensionId(String dimensionId) {
        DimensionId = dimensionId;
    }

    public String getDimensionValue() {
        return DimensionValue;
    }
    public void setDimensionValue(String dimensionValue) {
        DimensionValue = dimensionValue;
    }

}
