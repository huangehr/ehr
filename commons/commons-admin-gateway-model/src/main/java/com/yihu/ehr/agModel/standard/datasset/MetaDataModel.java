package com.yihu.ehr.agModel.standard.datasset;

/**
 * Created by wq on 2016/2/29.
 */
public class MetaDataModel {

    long id;                    // 数据元ID

    long dataSetId;             //数据集ID

    String innerCode;           // 内部标识码

    String name;                // 数据元名称

    String code;                 // 数据元编码

    String type;                // 数据类型

    long dictId;                //字典ID

    String dictName;

    String dictCode;

    String format;              // 数据格式

    String definition;          //说明

    String columnName;          // 字段名

    String columnLength;         // 数据长度

    String columnType;          // 数据类型

    boolean nullable;           // 是否可为空

    boolean primaryKey;         // 是否为主键

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDataSetId() {
        return dataSetId;
    }

    public void setDataSetId(long dataSetId) {
        this.dataSetId = dataSetId;
    }

    public String getInnerCode() {
        return innerCode;
    }

    public void setInnerCode(String innerCode) {
        this.innerCode = innerCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getDictId() {
        return dictId;
    }

    public void setDictId(long dictId) {
        this.dictId = dictId;
    }

    public String getDictName() {
        return dictName;
    }

    public void setDictName(String dictName) {
        this.dictName = dictName;
    }

    public String getDictCode() {
        return dictCode;
    }

    public void setDictCode(String dictCode) {
        this.dictCode = dictCode;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnLength() {
        return columnLength;
    }

    public void setColumnLength(String columnLength) {
        this.columnLength = columnLength;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }
}
