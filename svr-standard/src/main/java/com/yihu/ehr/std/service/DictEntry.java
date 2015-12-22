package com.yihu.ehr.std.service;

import com.yihu.ehr.std.data.SQLGeneralDAO;
import java.util.Objects;

/**
 * 字典项
 * @version 1.0
 * @created 16-7月-2015 20:56:04
 */
public class DictEntry extends SQLGeneralDAO {

	String code;

	/**
	 * 字典ID
	 */
	long dictId;
	String value;
	@DifferIgnored

    Dict dict;
	String stdVersion;

	@DifferIgnored
	long id;
	String desc;

	@DifferIgnored
	Dict m_Dict;

	@DifferIgnored
	int hashCode;

	@DifferIgnored
	String dict_code;

	public String getDictCode() {
		return dict_code;
	}

	public void setDictCode(String dict_code) {
		this.dict_code = dict_code;
	}

	public DictEntry(){
		this.OperationType="";
	}

	public void finalize() throws Throwable {

	}

	public long getId(){return id;}

	public void setId(long id){
		this.id=id;
	};

	public String getCode(){
		return code;
	}

	public Dict getDict(){
		return dict;
	}


	public String getValue(){
		return value;
	}

	public String getStdVersion(){return stdVersion;}
	/**
	 * 
	 * @param code
	 */
	public void setCode(String code){
		this.code = code;
	}

	/**
	 * 
	 * @param xDict
	 */
	public void setDict(Dict xDict){
		this.dict =xDict;
	}

	/**
	 * 
	 * @param value
	 */
	public void setValue(String value){
		this.value=value;
	}

	public void setStdVersion(String stdVersion){
		this.stdVersion=stdVersion;
	}

	public String getDesc(){
		return desc;
	};

	public void setDesc(String desc){
		this.desc=desc;
	};

	public int getHashCode(){
		hashCode = Objects.hash(dict.getId(),  code, value, desc);

		return hashCode;
	};

	public void setHashCode(int hashCode){
		this.hashCode=hashCode;
	};

	public long getDictId() {
		return dictId;
	}

	public void setDictId(long dictId) {
		this.dictId = dictId;
	}

	public String getOperationType() {
		return OperationType;
	}

	public void setOperationType(String operationType) {
		OperationType = operationType;
	}

	String OperationType;
}//end DictEntry