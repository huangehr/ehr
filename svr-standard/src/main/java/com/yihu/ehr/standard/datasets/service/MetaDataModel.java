//package com.yihu.ehr.standard.datasets.service;
//
//
//import com.yihu.ehr.standard.dict.service.DictForInterface;
//
//import java.util.List;
//
///**
// * Created by wq on 2015/9/22.
// */
//public class MetaDataModel {
//
//    long id;                    // 数据元ID
//    long dataSetIds;
//    String code;                 // 标准元编码
//    String innerCode;           // 内部代码
//    String name;                // 名称
//    String type;                // 数据元数据类型
//    String format;              // 表示格式
//    long dictId;
//    String dictName;
//    List<DictForInterface> dictForInterface;
//    String definition;          // 标准元定义, 即说明
//    boolean nullable;           // 是否可为空
//    String columnLength;         // 数据长度
//    String columnType;          // 数据类型
//    String columnName;          // 字段名
//    boolean primaryKey;         // 是否为主键
//    int hashCode;
//
//    public long getId() {
//        return id;
//    }
//
//    public void setId(long id) {
//        this.id = id;
//    }
//
//    public long getDataSetIds() {
//        return dataSetIds;
//    }
//
//    public void setDataSetIds(long dataSetIds) {
//        this.dataSetIds = dataSetIds;
//    }
//
//    public String getCode() {
//        return code;
//    }
//
//    public void setCode(String code) {
//        this.code = code;
//    }
//
//    public String getInnerCode() {
//        return innerCode;
//    }
//
//    public void setInnerCode(String innerCode) {
//        this.innerCode = innerCode;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getType() {
//        return type;
//    }
//
//    public void setType(String type) {
//        this.type = type;
//    }
//
//    public String getFormat() {
//        return format;
//    }
//
//    public void setFormat(String format) {
//        this.format = format;
//    }
//
//    public long getDictId() {
//        return dictId;
//    }
//
//    public void setDictId(long dictId) {
//        this.dictId = dictId;
//    }
//
//    public List<DictForInterface> getDictForInterface() {
//        return dictForInterface;
//    }
//
//    public void setDictForInterface(List<DictForInterface> dictForInterface) {
//        dictForInterface = dictForInterface;
//    }
//
//    public String getDefinition() {
//        return definition;
//    }
//
//    public void setDefinition(String definition) {
//        this.definition = definition;
//    }
//
//    public boolean isNullable() {
//        return nullable;
//    }
//
//    public void setNullable(boolean nullable) {
//        this.nullable = nullable;
//    }
//
//    public String getColumnLength() {
//        return columnLength;
//    }
//
//    public void setColumnLength(String columnLength) {
//        this.columnLength = columnLength;
//    }
//
//    public String getColumnType() {
//        return columnType;
//    }
//
//    public void setColumnType(String columnType) {
//        this.columnType = columnType;
//    }
//
//    public String getColumnName() {
//        return columnName;
//    }
//
//    public void setColumnName(String columnName) {
//        this.columnName = columnName;
//    }
//
//    public boolean isPrimaryKey() {
//        return primaryKey;
//    }
//
//    public void setPrimaryKey(boolean primaryKey) {
//        this.primaryKey = primaryKey;
//    }
//
//    public int getHashCode() {
//        return hashCode;
//    }
//
//    public void setHashCode(int hashCode) {
//        this.hashCode = hashCode;
//    }
//
//    public String getDictName() {
//        return dictName;
//    }
//
//    public void setDictName(String dictName) {
//        this.dictName = dictName;
//    }
//}
