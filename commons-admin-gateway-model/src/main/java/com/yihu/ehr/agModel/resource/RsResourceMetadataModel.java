package com.yihu.ehr.agModel.resource;

/**
 * Created by lyr on 2016/5/4.
 */
public class RsResourceMetadataModel {
    private String id;
    private String resourcesId;
    private String metadataId;
    private String groupType;
    private String groupData;
    private String description;
    private String dictId;

    private String stdCode;
    private String name;
    private String columnType;

    public String getId()
    {
        return id;
    }
    public void setId(String id){
        this.id = id;
    }

    public String getResourcesId()
    {
        return resourcesId;
    }
    public void setResourcesId(String resourcesId)
    {
        this.resourcesId = resourcesId;
    }

    public String getMetadataId()
    {
        return metadataId;
    }
    public void setMetadataId(String metadataId)
    {
        this.metadataId = metadataId;
    }

    public String getDescription()
    {
        return  description;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getGroupType()
    {
        return  groupType;
    }
    public void setGroupType(String groupType)
    {
        this.groupType = groupType;
    }

    public String getGroupData()
    {
        return  groupData;
    }
    public void setGroupData(String groupData)
    {
        this.groupData = groupData;
    }

    public String getStdCode() {
        return stdCode;
    }

    public void setStdCode(String stdCode) {
        this.stdCode = stdCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public String getDictId() {
        return dictId;
    }

    public void setDictId(String dictId) {
        this.dictId = dictId;
    }
}
