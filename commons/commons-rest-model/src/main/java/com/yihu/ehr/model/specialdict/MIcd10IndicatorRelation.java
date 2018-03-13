package com.yihu.ehr.model.specialdict;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 医生
 *
 * @author Sand
 * @version 1.0
 * @created 02-6月-2015 17:38:05
 */
public class MIcd10IndicatorRelation implements Serializable{

	public MIcd10IndicatorRelation() {
	}

	private Long id;
	private Long icd10Id;
	private Long indicatorId;
    private String createUser;
    private Date createDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getIcd10Id() {
		return icd10Id;
	}

	public void setIcd10Id(Long icd10Id) {
		this.icd10Id = icd10Id;
	}

	public Long getIndicatorId() {
		return indicatorId;
	}

	public void setIndicatorId(Long indicatorId) {
		this.indicatorId = indicatorId;
	}

    public String getCreateUser() {
        return createUser;
    }
    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getCreateDate() {
        return createDate;
    }
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}