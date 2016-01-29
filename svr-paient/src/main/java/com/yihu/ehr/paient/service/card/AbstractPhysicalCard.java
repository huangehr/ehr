package com.yihu.ehr.paient.service.card;


import com.yihu.ehr.paient.service.demographic.DemographicId;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * 卡抽象类，仅作为继承时的数据成员使用
 * @author Witness
 * @version 1.0
 * @created 25-5月-2015 17:47:13
 */
@Entity
@Table(name = "physical_cards")
@Access(value = AccessType.PROPERTY)
public class AbstractPhysicalCard extends AbstractCard implements Serializable {

	  String id;				    // 卡ID
	  String number;				// 卡号
	  String ownerName;			// 持有人姓名
	  String status;			// 状态 CardStatus
	  String type;				// 类型 CardType
	  String description;			// 描述
	  Date createDate;				// 创建日期
	  //DemographicId demographicId;	// 人口学ID
	  String idCardNo;
	  String DType;

	//特殊字段
	  String local;				// 发行地/归属地
	  String releaseOrg;				// 发行机构
	  Date releaseDate;			// 发行时间
	  Date validityDateBegin;		// 有效期起始时间
	  Date validityDateEnd;		// 有效期结束时间

	public AbstractPhysicalCard() {
		id  = UUID.randomUUID().toString().replace("-","");
	}


	@Id
	@GeneratedValue(generator = "Generator")
	@GenericGenerator(name = "Generator", strategy = "assigned")
	@Column(name = "id", unique = true, nullable = false)
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	@Column(name = "number",nullable = false )
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}

	@Column(name = "owner_name", nullable = true)
	public String getOwnerName() {
		return ownerName;
	}
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	@Column(name = "card_status", nullable = true)
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	@Column(name = "card_type", nullable = true)
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	@Column(name = "description", nullable = true)
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "create_date", nullable = true)
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

//	@Column(name = "id_card_no", nullable = true)
//	public DemographicId getDemographicId() {
//		return demographicId;
//	}
//	public void setDemographicId(DemographicId demographicId) {
//		this.demographicId = demographicId;
//	}


	@Column(name = "id_card_no", nullable = true)
	public String getIdCardNo() {
		return idCardNo;
	}
	public void setIdCardNo(String idCardNo) {
		this.idCardNo = idCardNo;
	}

	@Column(name = "DType", nullable = true)
	public String getDType() {
		return DType;
	}
	public void setDType(String DType) {
		this.DType = DType;
	}

	@Column(name = "local", nullable = true)
	public String getLocal() {
		return local;
	}
	public void setLocal(String local) {
		this.local = local;
	}

	@Column(name = "release_org", nullable = true)
	public String getReleaseOrg() {
		return releaseOrg;
	}
	public void setReleaseOrg(String releaseOrg) {
		this.releaseOrg = releaseOrg;
	}

	@Column(name = "release_date", nullable = true)
	public Date getReleaseDate() {
		return releaseDate;
	}
	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}
	@Column(name = "validity_date_begin", nullable = true)
	public Date getValidityDateBegin() {
		return validityDateBegin;
	}
	public void setValidityDateBegin(Date validityDateBegin) {
		this.validityDateBegin = validityDateBegin;
	}

	@Column(name = "validity_date_end", nullable = true)
	public Date getValidityDateEnd() {
		return validityDateEnd;
	}
	public void setValidityDateEnd(Date validityDateEnd) {
		this.validityDateEnd = validityDateEnd;
	}
}