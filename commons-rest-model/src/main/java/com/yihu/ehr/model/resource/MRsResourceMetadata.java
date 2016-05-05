package com.yihu.ehr.model.resource;

/**
 * Created by lyr on 2016/5/4.
 */
public class MRsResourceMetadata {
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

    public String getColumnCode()
    {
        return columnCode;
    }
    public void setColumnCode(String columnCode)
    {
        this.columnCode = columnCode;
    }

    public String getColumnName()
    {
        return columnName;
    }
    public void setColumnName(String columnName)
    {
        this.columnName = columnName;
    }

    public String getColumnType()
    {
        return this.columnType;
    }
    public void setColumnType(String columnType)
    {
        this.columnType =  columnType;
    }

    public String getDefinition()
    {
        return definition;
    }
    public void setDefinition(String definition)
    {
        this.definition = definition;
    }

    public int getLength()
    {
        return length;
    }
    public void setLength(int length)
    {
        this.length = length;
    }

    public String getType()
    {
        return type;
    }
    public void setType(String type)
    {
        this.type = type;
    }

    public String getFormat()
    {
        return format;
    }
    public void setFormat(String format)
    {
        this.format = format;
    }

    public String getPrimaryKey()
    {
        return primaryKey;
    }
    public void setPrimaryKey(String primaryKey)
    {
        this.primaryKey = primaryKey;
    }

    public String getNullAble()
    {
        return nullAble;
    }
    public void setNullAble(String nullAble)
    {
        this.nullAble = nullAble;
    }

    public String getDimensionId()
    {
        return dimensionId;
    }
    public void setDimensionId(String dimensionId)
    {
        this.dimensionId = dimensionId;
    }

    public String getRelated()
    {
        return  related;
    }
    public void setRelated(String related)
    {
        this.related = related;
    }
}
