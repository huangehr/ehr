package com.yihu.ehr.resource.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by Sxy on 2017/08/04.
 * 资源列表
 */
@Entity
@Table(name = "rs_resources_query")
@Access(value = AccessType.PROPERTY)
public class RsResourcesQuery {
    private String id;
    private String resourcesId;
    private String query;
    private int resourcesType;

    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "assigned")
    @Column(name = "id", unique = true, nullable = false)
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }


    @Column(name="resources_id",nullable = false)
    public String getResourcesId() {
        return resourcesId;
    }

    public void setResourcesId(String resourcesId) {
        this.resourcesId = resourcesId;
    }

    @Column(name="query",nullable = false)
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Column(name="resources_type",nullable = false)
    public int getResourcesType() {
        return resourcesType;
    }

    public void setResourcesType(int resourcesType) {
        this.resourcesType = resourcesType;
    }
}
