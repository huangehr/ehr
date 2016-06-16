package com.yihu.ehr.resource.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
/**
 * 资源字典项
 *
 * Created by lyr on 2016/5/13.
 */
@Entity
@Table(name = "rs_dictionary_entry")
public class RsDictionaryEntry {
    private String id;
    private String dictId;
    private String dictCode;
    private String code;
    private String name;
    private String description;

    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "assigned")
    @Column(name = "id", unique = true, nullable = false)
    public String getId() {
        return id;
    }
    public void setId(String id)
    {
        this.id = id;
    }

    @Column(name = "dict_id")
    public String getDictId() {
        return dictId;
    }
    public void setDictId(String dictId) {
        this.dictId = dictId;
    }

    @Column(name = "dict_code",nullable = false)
    public String getDictCode()
    {
        return dictCode;
    }
    public void setDictCode(String dictCode)
    {
        this.dictCode = dictCode;
    }

    @Column(name = "code",nullable = false)
    public String getCode()
    {
        return code;
    }
    public void setCode(String code)
    {
        this.code = code;
    }

    @Column(name="name",nullable = false)
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    @Column(name="description")
    public String getDescription()
    {
        return description;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }
}
