package com.yihu.ehr.model.resource;

/**
 * Created by lyr on 2016/5/4.
 */
public class MRsResourceMetadata {
    private String id;
    private String resourcesId;
    private String metadataId;
    private String statsType;
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

    public String getStatsType()
    {
        return  statsType;
    }
    public void setStatsType(String statsType)
    {
        this.statsType = statsType;
    }
}
