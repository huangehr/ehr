package com.yihu.ehr.standard.dict.service;


import java.util.Objects;

/**
 * 字典项
 * @version 1.0
 * @created 16-7月-2015 20:56:04
 */
public class DictEntry{


	long id;
	long dictId;//字典ID
	String code;
	String value;
	String desc;
	int hashCode;

	String OperationType;
	Dict dict;
	String stdVersion;
	Dict mDict;
	String dictCode;

	public DictEntry(){
		this.OperationType="";
	}



	public String getDictCode() {
		return dictCode;
	}
	public void setDictCode(String dictCode) {
		this.dictCode = dictCode;
	}


	public long getId(){return id;}
	public void setId(long id){
		this.id=id;
	};

	public long getDictId() {
		return dictId;
	}
	public void setDictId(Long dictId) {
		if(dictId==null)
			this.dictId = 0;
		else
			this.dictId = dictId;
	}

	public String getCode(){
		return code;
	}
	public void setCode(String code){
		this.code = code;
	}

	public String getDesc(){
		return desc;
	};
	public void setDesc(String desc){
		this.desc=desc;
	};

	public String getValue(){
		return value;
	}
	public void setValue(String value){
		this.value=value;
	}

	public int getHashCode(){
		hashCode = Objects.hash(dict.getId(),  code, value, desc);
		return hashCode;
	};
	public void setHashCode(int hashCode){
		this.hashCode=hashCode;
	};

	public String getOperationType() {
		return OperationType;
	}
	public void setOperationType(String operationType) {
		OperationType = operationType;
	}

	public Dict getDict(){
		return dict;
	}
	public void setDict(Dict dict){
		this.dict=dict;
	}
//	public String getStdVersion(){return stdVersion;}

//	public void setStdVersion(String stdVersion){
//		this.stdVersion=stdVersion;
//	}










}