package com.yihu.ehr.std.service;

import com.yihu.ehr.constrant.Services;
import com.yihu.ehr.lang.ServiceFactory;

import java.util.Objects;

/**
 * 抽象数据元类.
 * @author Witness
 * @version 1.0
 * @created 06-7月-2015 15:47:27
 */
public class MetaData {

    @DifferIgnored
    long id;                    // 数据元ID
	String code;                 // 标准元编码
	String definition;          // 标准元定义, 即说明
	String format;              // 表示格式
	String innerCode;           // 内部代码
	String name;                // 名称
	String type;                // 数据元数据类型

    @DifferIgnored
    DataSet dataSet;           // 所属数据集

    long dataSetId;

    long dictId;

    @DifferIgnored
    int hashCode;

    String columnLength;         // 数据长度
    String columnType;          // 数据类型
    String columnName;          // 字段名
    boolean nullable;           // 是否可为空
    boolean primaryKey;         // 是否为主键
    boolean isHbaseFullTextRetrieval;       // Hbase 全文检索字段
    boolean isHbasePrimaryKey;              // Hbase 主键字段

    String dict_name;
    String dict_code;


    public String getDict_code() {
        return dict_code;
    }


    public void setDict_code(String dict_code) {
        this.dict_code = dict_code;
    }

    public String getDict_name() {
        return dict_name;
    }

    public void setDict_name(String dict_name) {
        this.dict_name = dict_name;
    }

    public String getOperationType() {
        return OperationType;
    }

    public void setOperationType(String operationType) {
        OperationType = operationType;
    }

    String OperationType;

    public MetaData(){
        this.OperationType="";
	}

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

    // MetaDataMapping

	public String getColumnLength(){
		return columnLength;
	}


	public String getColumnType(){
		return columnType;
	}


    public String getColumnName() { return columnName; }


	public boolean isNullable(){
		return nullable;
	}


	public boolean isPrimaryKey(){
		return primaryKey;
	}


	public void setColumnLength(String length){
        this.columnLength = length;
	}


	public void setColumnType(String type){
        this.columnType = type;
	}


    public void setColumnName(String columnName){
        this.columnName = columnName;
    }


	public void setNullable(boolean nullable){
        this.nullable = nullable;
	}


	public void setPrimaryKey(boolean isPrimaryKey){
        this.primaryKey = isPrimaryKey;
	}

    // MetaData

	public String getCode(){
		return code;
	}


	public DataSet getDataSet(){
		return dataSet;
	}


	public String getDefinition(){
		return definition;
	}


	public Dict getDict(){
        DictManager dictManager = ServiceFactory.getService(Services.DictManager);
        return dictManager.getDict(dictId, getDataSet().getInnerVersion());
	}


	public String getFormat(){
		return format;
	}


	public int getHashCode(){
        hashCode = Objects.hash(dataSet.getId(), dictId, code, definition, format, innerCode, name,
                type, columnLength, columnType, nullable, primaryKey);

        return hashCode;
	}


	public long getId(){
		return id;
	}


	public String getInnerCode(){
		return innerCode;
	}


	public String getName(){
		return name;
	}


	public String getType(){
		return type;
	}

    public void setId(long id){
        this.id = id;
    }


	public void setCode(String code){
        this.code = code;
	}


	public void setDefinition(String definition){
        this.definition = definition;
	}


	public void setDict(Dict dict){
        this.dictId = dict.getId();
	}


    public void setDataSet(DataSet dataSet){
        this.dataSet = dataSet;
    }


	public void setFormat(String format){
        this.format = format;
	}


	public void setName(String name){
        this.name = name;
	}


	public void setType(String type){
        this.type = type;
	}


    public void setInnerCode(String innerCode){
        this.innerCode = innerCode;
    }

    public void setHashCode(int hashCode){
        this.hashCode = hashCode;
    }


    public boolean isHbaseFullTextRetrieval(){
        return isHbaseFullTextRetrieval;
    }


    public void setHbaseFullTextRetrieval(boolean as){
        isHbaseFullTextRetrieval = as;
    }


    public boolean isHbasePrimaryKey(){
        return isHbasePrimaryKey;
    }


    public void setHbasePrimaryKey(boolean as){
        isHbasePrimaryKey = as;
    }
}