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
public class MHealthProblemDict implements Serializable{

	public MHealthProblemDict() {
	}

	private Long id;
	private String code;
	private String name;
	private String description;
    private String createUser;
    private Date createDate;
    private String updateUser;
    private Date updateDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

    public String getUpdateUser() {
        return updateUser;
    }
    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public Date getUpdateDate() {
        return updateDate;
    }
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}