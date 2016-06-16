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
public class Icd10DrugRelationModel implements Serializable{

	public Icd10DrugRelationModel() {
	}

	private long id;
	private long icd10Id;
	private long drugId;
    private String createUser;
    private Date createDate;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getIcd10Id() {
		return icd10Id;
	}

	public void setIcd10Id(long icd10Id) {
		this.icd10Id = icd10Id;
	}

	public long getDrugId() {
		return drugId;
	}

	public void setDrugId(long drugId) {
		this.drugId = drugId;
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