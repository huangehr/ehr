package com.yihu.ehr.standard.datasets.service;


import com.yihu.ehr.standard.dict.service.Dict;

import java.util.Objects;

/**
 * ��������Ԫ��.
 * @author Witness
 * @version 1.0
 * @created 06-7��-2015 15:47:27
 */
public class MetaData {

    long id;                    // ����ԪID
	String code;                 // ��׼Ԫ����
	String definition;          // ��׼Ԫ����, ��˵��
	String format;              // ��ʾ��ʽ
	String innerCode;           // �ڲ�����
	String name;                // ����
	String type;                // ����Ԫ��������

    DataSet dataSet;           // �������ݼ�

    long dataSetId;

    long dictId;

    int hashCode;

    String columnLength;         // ���ݳ���
    String columnType;          // ��������
    String columnName;          // �ֶ���
    boolean nullable;           // �Ƿ��Ϊ��
    boolean primaryKey;         // �Ƿ�Ϊ����
    boolean isHbaseFullTextRetrieval;       // Hbase ȫ�ļ����ֶ�
    boolean isHbasePrimaryKey;              // Hbase �����ֶ�

    String dictName;
    String dictCode;

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

    // XMetaDataMapping
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

    // XMetaData
	public String getCode(){
		return code;
	}

	public DataSet getDataSet(){
		return dataSet;
	}

	public String getDefinition(){
		return definition;
	}

//    @Override
//	public XDict getDict(){
//        XDictManager dictManager = ServiceFactory.getService(Services.DictManager);
//        return dictManager.getDict(dictId, getDataSet().getInnerVersion());
//	}

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