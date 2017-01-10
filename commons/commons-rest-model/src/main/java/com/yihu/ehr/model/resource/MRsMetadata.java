package com.yihu.ehr.model.resource;

/**
 * Created by lyr on 2016/5/16.
 */
public class MRsMetadata {
    private String id;
    private String domain;
    private String name;
    private String stdCode;
    private String displayCode;
    private String columnType;
    private String nullAble;
    private String dictCode;
    private String description;
    private String valid;
    private int dictId;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getDomain() {
        return domain;
    }
    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getStdCode() {
        return stdCode;
    }
    public void setStdCode(String stdCode) {
        this.stdCode = stdCode;
    }

    public String getDisplayCode() {
        return displayCode;
    }
    public void setDisplayCode(String displayCode) {
        this.displayCode = displayCode;
    }

    public String getDictCode() {
        return dictCode;
    }
    public void setDictCode(String dictCode) {
        this.dictCode = dictCode;
    }

    public String getColumnType() {
        return columnType;
    }
    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public String getNullAble() {
        return nullAble;
    }
    public void setNullAble(String nullAble) {
        this.nullAble = nullAble;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getValid() {
        return valid;
    }
    public void setValid(String valid) {
        this.valid = valid;
    }

    public int getDictId() {
        return dictId;
    }
    public void setDictId(int dictId) {
        this.dictId = dictId;
    }
}
