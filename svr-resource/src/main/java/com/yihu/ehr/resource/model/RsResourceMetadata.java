package com.yihu.ehr.resource.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by lyr on 2016/4/25.
 */
@Entity
@Table(name="rs_resource_metadata")
public class RsResourceMetadata {
    private String id;
    private String resourcesId;
    private String metadataId;
    private String statsType;
    private String description;

    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "assigned")
    @Column(name = "id", unique = true, nullable = false)
    public String getId()
    {
        return id;
    }
    public void setId(String id){
        this.id = id;
    }

    @Column(name="resources_id",nullable = false)
    public String getResourcesId()
    {
        return resourcesId;
    }
    public void setResourcesId(String resourcesId)
    {
        this.resourcesId = resourcesId;
    }

    @Column(name="metadata_id",nullable = false)
    public String getMetadataId()
    {
        return metadataId;
    }
    public void setMetadataId(String metadataId)
    {
        this.metadataId = metadataId;
    }

    @Column(name="description")
    public String getDescription()
    {
        return  description;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }

    @Column(name="stats_type")
    public String getStatsType()
    {
        return  statsType;
    }
    public void setStatsType(String statsType)
    {
        this.statsType = statsType;
    }
}
