package com.yihu.ehr.model.resource;

/**
 * Created by lyr on 2016/5/16.
 */
public class MRsDictionaryEntry {
    private String id;
    private String dictId;
    private String dictCode;
    private String code;
    private String name;
    private String description;

    public String getId() {
        return id;
    }
    public void setId(String id)
    {
        this.id = id;
    }

    public String getDictId() {
        return dictId;
    }
    public void setDictId(String dictId) {
        this.dictId = dictId;
    }

    public String getDictCode()
    {
        return dictCode;
    }
    public void setDictCode(String dictCode)
    {
        this.dictCode = dictCode;
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
}
