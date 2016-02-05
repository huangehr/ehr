package com.yihu.ehr.standard.datasets.service;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.3
 */
public class MetaDataForInterface {


    String id;                    // 数据元ID
    String code;                 // 标准元编码
    String definition;          // 标准元定义, 即说明

    String formatType;              // 表示格式
    String innerCode;           // 内部代码
    String name;                // 名称
    String type;                // 数据元数据类型

    String datasetId;

    String version;

    String dictId;

    String hashCode;

    String columnLength;         // 数据长度
    String columnType;          // 数据类型
    String columnName;          // 字段名
    String nullable;           // 是否可为空
    String primaryKey;         // 是否为主键
    String dictName;

    public String getOperationType() {
        return OperationType;
    }

    public void setOperationType(String operationType) {
        OperationType = operationType;
    }

    String OperationType;

    public MetaDataForInterface() {
        this.OperationType = "";
    }

    public String getDictId() {
        return dictId;
    }

    public void setDictId(String dictId) {
        this.dictId = dictId;
    }

    public String getDictName() {
        return dictName;
    }

    public void setDictName(String dictName) {
        this.dictName = dictName;
    }


    public String getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(String datasetId) {
        this.datasetId = datasetId;
    }

    public String getColumnLength() {
        return columnLength;
    }

    public String getColumnType() {
        return columnType;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getNullable() {
        return nullable;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setColumnLength(String length) {
        this.columnLength = length;
    }

    public void setColumnType(String type) {
        this.columnType = type;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public void setNullable(String nullable) {
        this.nullable = nullable;
    }

    public void setPrimaryKey(String isPrimaryKey) {
        this.primaryKey = isPrimaryKey;
    }

    // XMetaData

    public String getCode() {
        return code;
    }

    public String getDefinition() {
        return definition;
    }

    public String getHashCode() {

        return hashCode;
    }

    public String getId() {
        return id;
    }

    public String getInnerCode() {
        return innerCode;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setInnerCode(String innerCode) {
        this.innerCode = innerCode;
    }

    public void setHashCode(String hashCode) {
        this.hashCode = hashCode;
    }

    public String getFormatType() {
        return formatType;
    }

    public void setFormatType(String formatType) {
        this.formatType = formatType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}