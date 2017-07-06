package com.yihu.quota.model.jpa.dict;// default package

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * SystemDictList entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "system_dict_list")
public class SystemDictList implements java.io.Serializable {

	// Fields

	private Integer id;
	private String dictName;
	private String chineseName;
	private String pyCode;
	private String pid;
	private String remark;
	private String relationTable;
	private String relationColCode;
	private String relationColValue;
	private String relationColExtend;

	// Constructors

	/** default constructor */
	public SystemDictList() {
	}

	/** minimal constructor */
	public SystemDictList(Integer id, String dictName, String chineseName,
			String pid) {
		this.id = id;
		this.dictName = dictName;
		this.chineseName = chineseName;
		this.pid = pid;
	}

	/** full constructor */
	public SystemDictList(Integer id, String dictName, String chineseName,
			String pyCode, String pid, String remark, String relationTable,
			String relationColCode, String relationColValue,
			String relationColExtend) {
		this.id = id;
		this.dictName = dictName;
		this.chineseName = chineseName;
		this.pyCode = pyCode;
		this.pid = pid;
		this.remark = remark;
		this.relationTable = relationTable;
		this.relationColCode = relationColCode;
		this.relationColValue = relationColValue;
		this.relationColExtend = relationColExtend;
	}

	// Property accessors
	@Id
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "dict_name", nullable = false, length = 50)
	public String getDictName() {
		return this.dictName;
	}

	public void setDictName(String dictName) {
		this.dictName = dictName;
	}

	@Column(name = "chinese_name", nullable = false, length = 50)
	public String getChineseName() {
		return this.chineseName;
	}

	public void setChineseName(String chineseName) {
		this.chineseName = chineseName;
	}

	@Column(name = "py_code", length = 50)
	public String getPyCode() {
		return this.pyCode;
	}

	public void setPyCode(String pyCode) {
		this.pyCode = pyCode;
	}

	@Column(name = "pid", nullable = false, length = 50)
	public String getPid() {
		return this.pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	@Column(name = "remark", length = 200)
	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Column(name = "relation_table", length = 50)
	public String getRelationTable() {
		return this.relationTable;
	}

	public void setRelationTable(String relationTable) {
		this.relationTable = relationTable;
	}

	@Column(name = "relation_col_code", length = 50)
	public String getRelationColCode() {
		return this.relationColCode;
	}

	public void setRelationColCode(String relationColCode) {
		this.relationColCode = relationColCode;
	}

	@Column(name = "relation_col_value", length = 50)
	public String getRelationColValue() {
		return this.relationColValue;
	}

	public void setRelationColValue(String relationColValue) {
		this.relationColValue = relationColValue;
	}

	@Column(name = "relation_col_extend", length = 50)
	public String getRelationColExtend() {
		return this.relationColExtend;
	}

	public void setRelationColExtend(String relationColExtend) {
		this.relationColExtend = relationColExtend;
	}

}