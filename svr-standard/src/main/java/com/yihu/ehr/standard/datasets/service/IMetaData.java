package com.yihu.ehr.standard.datasets.service;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Objects;

/**
 * 抽象数据元类.
 *
 * @author lincl
 * @version 1.0
 * @created 2016.2.22
 */
@MappedSuperclass
public class IMetaData {

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

    /************************/
    /**  Transient        ***/
    /************************/
    boolean isHbaseFullTextRetrieval;       // Hbase 全文检索字段
    boolean isHbasePrimaryKey;              // Hbase 主键字段
    String dictName;
    String dictCode;
    String OperationType;


    public IMetaData() {
        this.OperationType = "";
    }

    @Column(name = "dict_id", unique = false, nullable = true)
    public long getDictId() {
        return dictId;
    }

    public void setDictId(long dictId) {
        this.dictId = dictId;
    }

    @Column(name = "dataset_id", unique = false, nullable = false)
    public long getDataSetId() {
        return dataSetId;
    }

    public void setDataSetId(long dataSetId) {
        this.dataSetId = dataSetId;
    }

    @Column(name = "column_length", unique = false, nullable = true)
    public String getColumnLength() {
        return columnLength;
    }

    public void setColumnLength(String length) {
        this.columnLength = length;
    }

    @Column(name = "column_type", unique = false, nullable = true)
    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String type) {
        this.columnType = type;
    }

    @Column(name = "column_name", unique = false, nullable = false)
    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    @Column(name = "nullable", unique = false, nullable = true)
    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    @Column(name = "primary_key", unique = false, nullable = true)
    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean isPrimaryKey) {
        this.primaryKey = isPrimaryKey;
    }

    @Column(name = "code", unique = false, nullable = false)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "definition", unique = false, nullable = true)
    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    @Column(name = "format", unique = false, nullable = true)
    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    @Column(name = "hash", unique = false, nullable = false)
    public int getHashCode() {
        hashCode = Objects.hash(dataSetId, dictId, code, definition, format, innerCode, name,
                type, columnLength, columnType, nullable, primaryKey);

        return hashCode;
    }

    public void setHashCode(int hashCode) {
        this.hashCode = hashCode;
    }

    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "increment")
    @Column(name = "id", unique = true, nullable = false)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "inner_code", unique = false, nullable = false)
    public String getInnerCode() {
        return innerCode;
    }

    public void setInnerCode(String innerCode) {
        this.innerCode = innerCode;
    }

    @Column(name = "name", unique = false, nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "type", unique = false, nullable = true)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    /**************************************************************************************************/
    /**       Transient                                                                             **/
    /**                                                                                             **/
    /**************************************************************************************************/

    @Transient
    public boolean isHbaseFullTextRetrieval() {
        return isHbaseFullTextRetrieval;
    }

    public void setHbaseFullTextRetrieval(boolean as) {
        isHbaseFullTextRetrieval = as;
    }

    @Transient
    public boolean isHbasePrimaryKey() {
        return isHbasePrimaryKey;
    }

    public void setHbasePrimaryKey(boolean as) {
        isHbasePrimaryKey = as;
    }

    @Transient
    public String getDictCode() {
        return dictCode;
    }

    public void setDictCode(String dictCode) {
        this.dictCode = dictCode;
    }

    @Transient
    public String getDictName() {
        return dictName;
    }

    public void setDictName(String dictName) {
        this.dictName = dictName;
    }

    @Transient
    public String getOperationType() {
        return OperationType;
    }

    public void setOperationType(String operationType) {
        OperationType = operationType;
    }
}