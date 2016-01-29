package com.yihu.ehr.paient.service.card;

import com.yihu.ehr.model.address.MAddress;
import com.yihu.ehr.model.dict.MBaseDict;
import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.model.user.MUser;

import java.util.Date;
import java.util.UUID;

/**
 * 卡抽象类，仅作为继承时的数据成员使用
 * @author Witness
 * @version 1.0
 * @updated 11-6月-2015 9:51:15
 */
public abstract class AbstractCardModel {
	protected String id;				    // 卡ID
	protected String number;				// 卡号
	protected MUser ownerName;			// 持有人姓名
	protected MBaseDict status;			// 状态 CardStatus
	protected MBaseDict type;				// 类型 CardType
	protected String description;			// 描述
	protected Date createDate;				// 创建日期
	protected String demographicId;	// 人口学ID
	protected MAddress local;				// 发行地/归属地
	protected MOrganization releaseOrg;				// 发行机构
	protected Date releaseDate;			// 发行时间
	protected Date validityDateBegin;		// 有效期起始时间
	protected Date validityDateEnd;		// 有效期结束时间


	public AbstractCardModel(){;
	}


	public boolean checkIsVirtualCard(){
		if(this.getType().equals("VirtualCard")){
			return true;
		}else{
			return false;
		}
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

	public MUser getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(MUser ownerName) {
		this.ownerName = ownerName;
	}

	public MBaseDict getStatus() {
		return status;
	}

	public void setStatus(MBaseDict status) {
		this.status = status;
	}

	public MBaseDict getType() {
		return type;
	}

	public void setType(MBaseDict type) {
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

	public String getDemographicId() {
		return demographicId;
	}

	public void setDemographicId(String demographicId) {
		this.demographicId = demographicId;
	}

	public MAddress getLocal() {
		return local;
	}

	public void setLocal(MAddress local) {
		this.local = local;
	}

	public MOrganization getReleaseOrg() {
		return releaseOrg;
	}

	public void setReleaseOrg(MOrganization releaseOrg) {
		this.releaseOrg = releaseOrg;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}

	public Date getValidityDateBegin() {
		return validityDateBegin;
	}

	public void setValidityDateBegin(Date validityDateBegin) {
		this.validityDateBegin = validityDateBegin;
	}

	public Date getValidityDateEnd() {
		return validityDateEnd;
	}

	public void setValidityDateEnd(Date validityDateEnd) {
		this.validityDateEnd = validityDateEnd;
	}
}
