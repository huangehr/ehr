package com.yihu.ehr.model.resource;

/**
 * Created by lyr on 2016/5/16.
 */
public class MRsSystemDictionaryEntry {
    private String id;
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
