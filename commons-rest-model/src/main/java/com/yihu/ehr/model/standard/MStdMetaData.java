package com.yihu.ehr.model.standard;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Objects;

/**
 * 数据元数据模型
 *
 * @author lincl
 * @version 1.0
 * @created 2016.2.22
 */
public class MStdMetaData {

    long id;                    // 数据元ID
    long dataSetId;
    String code;                 // 标准元编码
    String innerCode;           // 内部代码
    String name;                // 名称
    String type;                // 数据元数据类型
    String format;              // 表示格式
    long dictId;
    String definition;          // 标准元定义, 即说明
    boolean nullable;           // 是否可为空
    String columnType;          // 数据类型
    String columnName;          // 字段名
    String columnLength;         // 数据长度
    boolean primaryKey;         // 是否为主键
    int hashCode;

    boolean isHbaseFullTextRetrieval;       // Hbase 全文检索字段
    boolean isHbasePrimaryKey;              // Hbase 主键字段
    String dictName;
    String dictCode;


    public long getDictId() {
        return dictId;
    }
    public void setDictId(long dictId) {
        this.dictId = dictId;
    }


    public long getDataSetId() {
        return dataSetId;
    }

    public void setDataSetId(long dataSetId) {
        this.dataSetId = dataSetId;
    }

    public String getColumnLength() {
        return columnLength;
    }

    public void setColumnLength(String length) {
        this.columnLength = length;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String type) {
        this.columnType = type;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
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

    public void setPrimaryKey(boolean isPrimaryKey) {
        this.primaryKey = isPrimaryKey;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getHashCode() {
        hashCode = Objects.hash(dataSetId, dictId, code, definition, format, innerCode, name,
                type, columnLength, columnType, nullable, primaryKey);

        return hashCode;
    }

    public void setHashCode(int hashCode) {
        this.hashCode = hashCode;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isHbaseFullTextRetrieval() {
        return isHbaseFullTextRetrieval;
    }

    public void setHbaseFullTextRetrieval(boolean as) {
        isHbaseFullTextRetrieval = as;
    }

    public boolean isHbasePrimaryKey() {
        return isHbasePrimaryKey;
    }

    public void setHbasePrimaryKey(boolean as) {
        isHbasePrimaryKey = as;
    }

    public String getDictCode() {
        return dictCode;
    }

    public void setDictCode(String dictCode) {
        this.dictCode = dictCode;
    }

    public String getDictName() {
        return dictName;
    }

    public void setDictName(String dictName) {
        this.dictName = dictName;
    }
}