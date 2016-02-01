package com.yihu.ehr.paient.service.card;

import com.yihu.ehr.paient.service.demographic.DemographicId;

import java.util.Date;
import java.util.UUID;

/**
 * 卡抽象类，仅作为继承时的数据成员使用
 * @author Witness
 * @version 1.0
 * @updated 11-6月-2015 9:51:15
 */
public  class AbstractCard {
	protected String id;				    // 卡ID
	protected String number;				// 卡号
	protected String ownerName;			// 持有人姓名
	protected String status;			// 状态 CardStatus
	protected String type;				// 类型 CardType
	protected String description;			// 描述
	protected Date createDate;				// 创建日期
	protected DemographicId demographicId;	// 人口学ID


	public AbstractCard(){;
		id  = UUID.randomUUID().toString().replace("-","");
		createDate = new Date();
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public DemographicId getDemographicId() {
		return demographicId;
	}

	public void setDemographicId(DemographicId demographicId) {
		this.demographicId = demographicId;
	}
}
