package com.yihu.ehr.model.specialdict;

import java.io.Serializable;
import java.util.Date;

/**
 * 医生
 *
 * @author Sand
 * @version 1.0
 * @created 02-6月-2015 17:38:05
 */
public class MIcd10HpRelation implements Serializable{

	public MIcd10HpRelation() {
	}

	private Long id;
	private Long hpId;
	private Long icd10Id;
    private String createUser;
    private Date createDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getHpId() {
		return hpId;
	}

	public void setHpId(Long hpId) {
		this.hpId = hpId;
	}

	public Long getIcd10Id() {
		return icd10Id;
	}

	public void setIcd10Id(Long icd10Id) {
		this.icd10Id = icd10Id;
	}

    public String getCreateUser() {
        return createUser;
    }
    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Date getCreateDate() {
        return createDate;
    }
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

}