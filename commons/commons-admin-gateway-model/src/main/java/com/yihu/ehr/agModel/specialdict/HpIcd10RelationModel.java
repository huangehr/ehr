package com.yihu.ehr.agModel.specialdict;

import java.io.Serializable;
import java.util.Date;

/**
 * 医生
 *
 * @author Sand
 * @version 1.0
 * @created 02-6月-2015 17:38:05
 */
public class HpIcd10RelationModel implements Serializable{

	public HpIcd10RelationModel() {
	}

	private long id;
	private long hpId;
	private long icd10Id;
    private String createUser;
    private Date createDate;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getHpId() {
		return hpId;
	}

	public void setHpId(long hpId) {
		this.hpId = hpId;
	}

	public long getIcd10Id() {
		return icd10Id;
	}

	public void setIcd10Id(long icd10Id) {
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