package com.yihu.ehr.resource.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * 资源系统字典
 *
 * Created by lyr on 2016/5/13.
 */
@Entity
@Table(name="rs_system_dictionary")
public class RsSystemDictionary {
    private String id;
    private String code;
    private String name;
    private String description;
    private String relatedTable;
    private String codeColumn;
    private String textColumn;
    private String expandColumn;

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

    @Column(name="related_table")
    public String getRelatedTable()
    {
        return relatedTable;
    }
    public void setRelatedTable(String relatedTable)
    {
        this.relatedTable = relatedTable;
    }

    @Column(name="code_column")
    public String getCodeColumn()
    {
        return codeColumn;
    }
    public void setCodeColumn(String codeColumn)
    {
        this.codeColumn = codeColumn;
    }

    @Column(name="text_column")
    public String getTextColumn()
    {
        return textColumn;
    }
    public void setTextColumn(String textColumn)
    {
        this.textColumn = textColumn;
    }

    @Column(name="expand_column")
    public String getExpandColumn()
    {
        return expandColumn;
    }
    public void setExpandColumn(String expandColumn)
    {
        this.expandColumn = expandColumn;
    }
}
