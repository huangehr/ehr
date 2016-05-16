package com.yihu.ehr.model.resource;

/**
 * Created by lyr on 2016/5/16.
 */
public class MRsSystemDictionary {
    private String id;
    private String code;
    private String name;
    private String description;
    private String relatedTable;
    private String codeColumn;
    private String textColumn;
    private String expandColumn;

    public String getId() {
        return id;
    }
    public void setId(String id)
    {
        this.id = id;
    }

    public String getCode()
    {
        return code;
    }
    public void setCode(String code)
    {
        this.code = code;
    }

    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getRelatedTable()
    {
        return relatedTable;
    }
    public void setRelatedTable(String relatedTable)
    {
        this.relatedTable = relatedTable;
    }

    public String getCodeColumn()
    {
        return codeColumn;
    }
    public void setCodeColumn(String codeColumn)
    {
        this.codeColumn = codeColumn;
    }

    public String getTextColumn()
    {
        return textColumn;
    }
    public void setTextColumn(String textColumn)
    {
        this.textColumn = textColumn;
    }

    public String getExpandColumn()
    {
        return expandColumn;
    }
    public void setExpandColumn(String expandColumn)
    {
        this.expandColumn = expandColumn;
    }

}
