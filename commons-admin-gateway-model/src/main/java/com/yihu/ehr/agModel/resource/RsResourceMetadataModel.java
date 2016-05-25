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
}
