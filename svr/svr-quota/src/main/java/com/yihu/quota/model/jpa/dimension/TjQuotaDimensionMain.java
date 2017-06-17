package com.yihu.quota.model.jpa.dimension;// default package

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * TjQuotaDimensionMain entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "tj_quota_dimension_main")
public class TjQuotaDimensionMain implements java.io.Serializable {

	// Fields

	private Integer id;
	private String quotaCode;
	private String mainCode;
	private String dictSql;
	private String keyVal;
	private String type;

	// Constructors

	/** default constructor */
	public TjQuotaDimensionMain() {
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "quota_code", length = 100)
	public String getQuotaCode() {
		return this.quotaCode;
	}

	public void setQuotaCode(String quotaCode) {
		this.quotaCode = quotaCode;
	}

	@Column(name = "main_code", length = 100)
	public String getMainCode() {
		return this.mainCode;
	}

	public void setMainCode(String mainCode) {
		this.mainCode = mainCode;
	}

	public String getDictSql() {
		return dictSql;
	}

	public void setDictSql(String dictSql) {
		this.dictSql = dictSql;
	}

	@Column(name = "key_val", length = 100)
	public String getKeyVal() {
		return keyVal;
	}

	public void setKeyVal(String keyVal) {
		this.keyVal = keyVal;
	}

	@Transient
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}