package com.yihu.ehr.patient.service.card;

import com.yihu.ehr.model.address.MGeography;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.org.MOrganization;

import java.util.Date;

/**
 * 卡抽象类，仅作为继承时的数据成员使用
 * @author Witness
 * @version 1.0
 * @updated 11-6月-2015 9:51:15
 */
public class MAbstractCard {


	String id;				    // 卡ID
	String number;				// 卡号
	String ownerName;			// 持有人姓名
	MConventionalDict status;			// 状态 CardStatus
	MConventionalDict type;				// 类型 CardType
	String description;			// 描述
	Date createDate;				// 创建日期
	String idCardNo;
	String DType;
	//特殊字段
	MGeography local;				// 发行地/归属地
	MOrganization releaseOrg;				// 发行机构
	Date releaseDate;			// 发行时间
	Date validityDateBegin;		// 有效期起始时间
	Date validityDateEnd;		// 有效期结束时间


	public MAbstractCard(){;
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

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public MConventionalDict getStatus() {
		return status;
	}

	public void setStatus(MConventionalDict status) {
		this.status = status;
	}

	public MConventionalDict getType() {
		return type;
	}

	public void setType(MConventionalDict type) {
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

	public String getIdCardNo() {
		return idCardNo;
	}

	public void setIdCardNo(String idCardNo) {
		this.idCardNo = idCardNo;
	}

	public String getDType() {
		return DType;
	}

	public void setDType(String DType) {
		this.DType = DType;
	}

	public MGeography getLocal() {
		return local;
	}

	public void setLocal(MGeography local) {
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
