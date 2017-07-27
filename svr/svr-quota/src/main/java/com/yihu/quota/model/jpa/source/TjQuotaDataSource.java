package com.yihu.quota.model.jpa.source;// default package

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * TjQuotaDataSource entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "tj_quota_data_source")
public class TjQuotaDataSource implements java.io.Serializable {

	// Fields

	private Integer id;
	private String quotaCode;
	private String sourceCode;
	private String configJson;
	private String code;

	// Constructors

	/** default constructor */
	public TjQuotaDataSource() {
	}

	/** full constructor */
	public TjQuotaDataSource(String quotaCode, String sourceCode,
			String configJson) {
		this.quotaCode = quotaCode;
		this.sourceCode = sourceCode;
		this.configJson = configJson;
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

	@Column(name = "source_code", length = 100)
	public String getSourceCode() {
		return this.sourceCode;
	}

	public void setSourceCode(String sourceCode) {
		this.sourceCode = sourceCode;
	}

	@Column(name = "config_json", length = 2000)
	public String getConfigJson() {
		return this.configJson;
	}

	public void setConfigJson(String configJson) {
		this.configJson = configJson;
	}

	@Transient
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}