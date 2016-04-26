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
    private String columnCode;
    private String columnName;
    private String columnType;
    private String definition;
    private int length;
    private String type;
    private String format;
    private String primaryKey;
    private String nullAble;
    private String dimensionId;
    private String related;

    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator",strategy = "assigned")
    @Column(name="id",unique = true,nullable = false)
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

    @Column(name="column_code",nullable = false)
    public String getColumnCode()
    {
        return columnCode;
    }
    public void setColumnCode(String columnCode)
    {
        this.columnCode = columnCode;
    }


    @Column(name="column_name",nullable = false)
    public String getColumnName()
    {
        return columnName;
    }
    public void setColumnName(String columnName)
    {
        this.columnName = columnName;
    }


    @Column(name="column_type",nullable = false)
    public String getColumnType()
    {
        return this.columnType;
    }
    public void setColumnType(String columnType)
    {
        this.columnType =  columnType;
    }

    @Column(name="definition")
    public String getDefinition()
    {
        return definition;
    }
    public void setDefinition(String definition)
    {
        this.definition = definition;
    }

    @Column(name="length")
    public int getLength()
    {
        return length;
    }
    public void setLength(int length)
    {
        this.length = length;
    }

    @Column(name="type")
    public String getType()
    {
        return type;
    }
    public void setType(String type)
    {
        this.type = type;
    }

    @Column(name="format")
    public String getFormat()
    {
        return format;
    }
    public void setFormat(String format)
    {
        this.format = format;
    }

    @Column(name="primary_key",nullable = false)
    public String getPrimaryKey()
    {
        return primaryKey;
    }
    public void setPrimaryKey(String primaryKey)
    {
        this.primaryKey = primaryKey;
    }

    @Column(name="null_able")
    public String getNullAble()
    {
        return nullAble;
    }
    public void setNullAble(String nullAble)
    {
        this.nullAble = nullAble;
    }

    @Column(name="dimension_id")
    public String getDimensionId()
    {
        return dimensionId;
    }
    public void setDimensionId(String dimensionId)
    {
        this.dimensionId = dimensionId;
    }

    @Column(name="related")
    public String getRelated()
    {
        return  related;
    }
    public void setRelated(String related)
    {
        this.related = related;
    }
}
